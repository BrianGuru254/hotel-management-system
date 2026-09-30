package com.hrms.backend.exception;

public class RefreshTokenReuseException extends RuntimeException {
    public RefreshTokenReuseException() {
        super("Refresh token reuse detected; the session has been revoked");
    }
}
