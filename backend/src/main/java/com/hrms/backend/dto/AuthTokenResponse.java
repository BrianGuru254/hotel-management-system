package com.hrms.backend.dto;

public record AuthTokenResponse(String tokenType, String accessToken, long expiresIn) {
}
