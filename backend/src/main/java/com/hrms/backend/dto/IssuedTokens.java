package com.hrms.backend.dto;

import java.time.Duration;

public record IssuedTokens(AuthTokenResponse accessToken, String rawRefreshToken, Duration refreshTokenLifetime) {
}
