package com.javanauta.user.infrastructure.exceptions;
import org.springframework.security.core.AuthenticationException;

import java.io.Serial;

public class UnauthorizedException extends AuthenticationException {

    @Serial
    private static final long serialVersionUID = 1L;


    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

