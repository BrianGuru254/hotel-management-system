package com.hrms.backend.controller;

import com.hrms.backend.dto.*;
import com.hrms.backend.service.AuthenticationService;
import com.hrms.backend.service.ScopeAuthorizationService;
import com.hrms.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    public static final String REFRESH_COOKIE = "hms_refresh";
    public static final String REQUEST_MARKER_HEADER = "X-Auth-Request";

    private final AuthenticationService authentication;
    private final ScopeAuthorizationService authorization;
    private final UserService users;
    private final boolean secureCookie;
    private final long refreshTtlSeconds;

    public AuthenticationController(AuthenticationService authentication, ScopeAuthorizationService authorization,
                                    UserService users,
                                    @Value("${app.security.jwt.cookie-secure:true}") boolean secureCookie,
                                    @Value("${app.security.jwt.refresh-token-ttl-seconds:2592000}") long refreshTtlSeconds) {
        this.authentication = authentication;
        this.authorization = authorization;
        this.users = users;
        this.secureCookie = secureCookie;
        this.refreshTtlSeconds = refreshTtlSeconds;
    }

    @PostMapping("/bootstrap")
    public ResponseEntity<AuthTokenResponse> bootstrap(@Valid @RequestBody BootstrapRequest request,
            @RequestHeader(REQUEST_MARKER_HEADER) String marker, HttpServletRequest servletRequest) {
        requireRequestMarker(marker);
        IssuedTokens tokens = authentication.bootstrap(request, userAgent(servletRequest), clientIp(servletRequest));
        return tokenResponse(tokens, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthTokenResponse> login(@Valid @RequestBody LoginRequest request,
            @RequestHeader(REQUEST_MARKER_HEADER) String marker, HttpServletRequest servletRequest) {
        requireRequestMarker(marker);
        IssuedTokens tokens = authentication.login(request, userAgent(servletRequest), clientIp(servletRequest));
        return tokenResponse(tokens, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthTokenResponse> refresh(
            @CookieValue(name = REFRESH_COOKIE, required = false) String refreshToken,
            @RequestHeader(REQUEST_MARKER_HEADER) String marker, HttpServletRequest servletRequest) {
        requireRequestMarker(marker);
        return tokenResponse(authentication.refresh(refreshToken, userAgent(servletRequest), clientIp(servletRequest)), HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = REFRESH_COOKIE, required = false) String refreshToken,
            @RequestHeader(REQUEST_MARKER_HEADER) String marker) {
        requireRequestMarker(marker);
        authentication.logout(refreshToken);
        return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, refreshCookie("", Duration.ZERO).toString()).build();
    }

    @GetMapping("/me")
    public UserResponse me() { return users.currentProfile(); }

    @PutMapping("/me")
    public UserResponse updateMe(@Valid @RequestBody UserUpdateRequest request) { return users.updateCurrentProfile(request); }

    @PatchMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authorization.requireSelfCapability("PASSWORD_CHANGE");
        authentication.changePassword(authorization.currentUserId(), request.currentPassword(), request.newPassword());
    }

    private ResponseEntity<AuthTokenResponse> tokenResponse(IssuedTokens issued, HttpStatus status) {
        return ResponseEntity.status(status)
                .header(HttpHeaders.SET_COOKIE, refreshCookie(issued.rawRefreshToken(), issued.refreshTokenLifetime()).toString())
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .header(HttpHeaders.PRAGMA, "no-cache")
                .body(issued.accessToken());
    }

    private ResponseCookie refreshCookie(String token, Duration lifetime) {
        return ResponseCookie.from(REFRESH_COOKIE, token)
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Strict")
                .path("/api/v1/auth")
                .maxAge(lifetime)
                .build();
    }

    private void requireRequestMarker(String marker) {
        if (!"true".equalsIgnoreCase(marker)) throw new AccessDeniedException("Missing same-origin request marker");
    }

    private String userAgent(HttpServletRequest request) { return request.getHeader(HttpHeaders.USER_AGENT); }
    private String clientIp(HttpServletRequest request) { return request.getRemoteAddr(); }
}
