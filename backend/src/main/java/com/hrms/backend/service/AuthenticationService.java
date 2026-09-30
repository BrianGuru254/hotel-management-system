package com.hrms.backend.service;

import com.hrms.backend.dto.BootstrapRequest;
import com.hrms.backend.dto.IssuedTokens;
import com.hrms.backend.dto.LoginRequest;
import com.hrms.backend.entity.HmsUser;
import com.hrms.backend.entity.RefreshToken;
import com.hrms.backend.entity.Role;
import com.hrms.backend.entity.ScopeType;
import com.hrms.backend.entity.UserRoleScope;
import com.hrms.backend.exception.DuplicateResourceException;
import com.hrms.backend.exception.InvalidCredentialsException;
import com.hrms.backend.exception.InvalidRefreshTokenException;
import com.hrms.backend.exception.RefreshTokenReuseException;
import com.hrms.backend.repository.HmsUserRepository;
import com.hrms.backend.repository.RefreshTokenRepository;
import com.hrms.backend.repository.RoleRepository;
import com.hrms.backend.repository.UserRoleScopeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthenticationService {
    private static final SecureRandom RANDOM = new SecureRandom();

    private final HmsUserRepository users;
    private final RoleRepository roles;
    private final UserRoleScopeRepository scopes;
    private final RefreshTokenRepository refreshTokens;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final JdbcTemplate jdbc;
    private final String issuer;
    private final String bootstrapSecret;
    private final long accessTtlSeconds;
    private final long refreshTtlSeconds;

    public AuthenticationService(HmsUserRepository users, RoleRepository roles, UserRoleScopeRepository scopes,
                                 RefreshTokenRepository refreshTokens, PasswordEncoder passwordEncoder,
                                 JwtEncoder jwtEncoder, JdbcTemplate jdbc,
                                 @Value("${app.security.jwt.issuer}") String issuer,
                                 @Value("${app.security.bootstrap-secret:}") String bootstrapSecret,
                                 @Value("${app.security.jwt.access-token-ttl-seconds:900}") long accessTtlSeconds,
                                 @Value("${app.security.jwt.refresh-token-ttl-seconds:2592000}") long refreshTtlSeconds) {
        this.users = users;
        this.roles = roles;
        this.scopes = scopes;
        this.refreshTokens = refreshTokens;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
        this.jdbc = jdbc;
        this.issuer = issuer;
        this.bootstrapSecret = bootstrapSecret;
        this.accessTtlSeconds = accessTtlSeconds;
        this.refreshTtlSeconds = refreshTtlSeconds;
    }

    @Transactional
    public IssuedTokens bootstrap(BootstrapRequest request, String deviceInfo, String ipAddress) {
        if (!matchesSecret(bootstrapSecret, request.bootstrapSecret())) {
            throw new InvalidCredentialsException();
        }
        Boolean completed = jdbc.queryForObject(
                "select completed from system_bootstrap_state where id = 1 for update", Boolean.class);
        if (Boolean.TRUE.equals(completed)) {
            throw new DuplicateResourceException("Initial system administrator has already been created");
        }
        validatePassword(request.password());
        if (users.existsByEmailIgnoreCase(request.email().trim())) {
            throw new DuplicateResourceException("Email address already exists");
        }
        HmsUser user = new HmsUser();
        user.setEmail(request.email().trim().toLowerCase());
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        Role role = roles.findById("SUPER_ADMIN").orElseThrow();
        UserRoleScope systemGrant = new UserRoleScope();
        systemGrant.setUser(user);
        systemGrant.setRole(role);
        systemGrant.setScopeType(ScopeType.SYSTEM);
        user.getRoleScopes().add(systemGrant);
        user = users.saveAndFlush(user);
        jdbc.update("update system_bootstrap_state set completed = TRUE, updated_at = CURRENT_TIMESTAMP where id = 1");
        return issue(user, null, deviceInfo, ipAddress);
    }

    @Transactional
    public IssuedTokens login(LoginRequest request, String deviceInfo, String ipAddress) {
        HmsUser user = users.findByEmailIgnoreCase(request.email().trim())
                .filter(HmsUser::getActive)
                .filter(candidate -> passwordEncoder.matches(request.password(), candidate.getPasswordHash()))
                .orElseThrow(InvalidCredentialsException::new);
        return issue(user, null, deviceInfo, ipAddress);
    }

    @Transactional(noRollbackFor = RefreshTokenReuseException.class)
    public IssuedTokens refresh(String rawToken, String deviceInfo, String ipAddress) {
        if (rawToken == null || rawToken.isBlank()) throw new InvalidRefreshTokenException();
        String hash = sha256(rawToken);
        RefreshToken previous = refreshTokens.findByTokenHashForUpdate(hash)
                .orElseThrow(InvalidRefreshTokenException::new);
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        if (previous.getRevokedAt() != null) {
            refreshTokens.revokeFamily(previous.getFamilyId(), now);
            throw new RefreshTokenReuseException();
        }
        if (!previous.getExpiresAt().isAfter(now) || !previous.getUser().getActive()) {
            previous.setRevokedAt(now);
            refreshTokens.save(previous);
            throw new InvalidRefreshTokenException();
        }
        previous.setRevokedAt(now);
        RefreshToken replacement = new RefreshToken();
        replacement.setUser(previous.getUser());
        replacement.setFamilyId(previous.getFamilyId());
        String nextRawToken = createRefreshToken();
        replacement.setTokenHash(sha256(nextRawToken));
        replacement.setExpiresAt(now.plusSeconds(refreshTtlSeconds));
        replacement.setDeviceInfo(limit(deviceInfo, 255));
        replacement.setIpAddress(limit(ipAddress, 45));
        replacement = refreshTokens.saveAndFlush(replacement);
        previous.setReplacedBy(replacement);
        refreshTokens.save(previous);
        return issue(previous.getUser(), nextRawToken, replacement);
    }

    @Transactional(noRollbackFor = RefreshTokenReuseException.class)
    public void logout(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) return;
        refreshTokens.findByTokenHashForUpdate(sha256(rawToken)).ifPresent(token ->
                refreshTokens.revokeFamily(token.getFamilyId(), OffsetDateTime.now(ZoneOffset.UTC)));
    }

    @Transactional
    public void changePassword(UUID userId, String currentPassword, String newPassword) {
        HmsUser user = users.findById(userId).orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) throw new InvalidCredentialsException();
        validatePassword(newPassword);
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        users.save(user);
        refreshTokens.revokeAllForUser(userId, OffsetDateTime.now(ZoneOffset.UTC));
    }

    private IssuedTokens issue(HmsUser user, String existingRaw, String deviceInfo, String ipAddress) {
        if (existingRaw != null) {
            RefreshToken token = new RefreshToken();
            token.setUser(user);
            token.setFamilyId(UUID.randomUUID());
            token.setTokenHash(sha256(existingRaw));
            token.setExpiresAt(OffsetDateTime.now(ZoneOffset.UTC).plusSeconds(refreshTtlSeconds));
            token.setDeviceInfo(limit(deviceInfo, 255));
            token.setIpAddress(limit(ipAddress, 45));
            refreshTokens.save(token);
            return issuedResponse(user, existingRaw);
        }
        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setFamilyId(UUID.randomUUID());
        String raw = createRefreshToken();
        token.setTokenHash(sha256(raw));
        token.setExpiresAt(OffsetDateTime.now(ZoneOffset.UTC).plusSeconds(refreshTtlSeconds));
        token.setDeviceInfo(limit(deviceInfo, 255));
        token.setIpAddress(limit(ipAddress, 45));
        refreshTokens.save(token);
        return issuedResponse(user, raw);
    }

    private IssuedTokens issue(HmsUser user, String rawRefresh, RefreshToken savedReplacement) {
        return issuedResponse(user, rawRefresh);
    }

    private IssuedTokens issuedResponse(HmsUser user, String rawRefresh) {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        List<UserRoleScope> userGrants = scopes.findByUserId(user.getId());
        LinkedHashSet<String> roleKeys = new LinkedHashSet<>();
        List<Map<String, Object>> scopeClaims = new ArrayList<>();
        for (UserRoleScope grant : userGrants) {
            roleKeys.add(grant.getRole().getRoleKey());
            Map<String, Object> claim = new HashMap<>();
            claim.put("role", grant.getRole().getRoleKey());
            claim.put("scope", grant.getScopeType().name());
            if (grant.getOrganization() != null) claim.put("organization_id", grant.getOrganization().getId().toString());
            if (grant.getBranch() != null) claim.put("branch_id", grant.getBranch().getId().toString());
            if (grant.getProperty() != null) claim.put("property_id", grant.getProperty().getId().toString());
            if (grant.getDepartment() != null) claim.put("department_id", grant.getDepartment().getId().toString());
            scopeClaims.add(claim);
        }
        Instant issuedAt = now.toInstant();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(user.getId().toString())
                .issuedAt(issuedAt)
                .expiresAt(issuedAt.plusSeconds(accessTtlSeconds))
                .id(UUID.randomUUID().toString())
                .claim("user_id", user.getId().toString())
                .claim("roles", List.copyOf(roleKeys))
                .claim("scopes", scopeClaims)
                .build();
        String access = jwtEncoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).type("JWT").build(), claims)).getTokenValue();
        IssuedTokens response = new IssuedTokens(
                new com.hrms.backend.dto.AuthTokenResponse("Bearer", access, accessTtlSeconds),
                rawRefresh, Duration.ofSeconds(refreshTtlSeconds));
        return response;
    }

    private void validatePassword(String password) {
        if (password == null || password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("Password must not exceed 72 UTF-8 bytes");
        }
    }

    private boolean matchesSecret(String expected, String actual) {
        if (expected == null || expected.isBlank() || actual == null) return false;
        return MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8), actual.getBytes(StandardCharsets.UTF_8));
    }

    private String createRefreshToken() {
        byte[] token = new byte[48];
        RANDOM.nextBytes(token);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(token);
    }

    private String sha256(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }

    private String limit(String value, int max) {
        if (value == null) return null;
        return value.length() <= max ? value : value.substring(0, max);
    }
}
