package com.javanauta.user.infrastructure.exceptions;

import java.io.Serial;

public class InvalidRequestException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;


    public InvalidRequestException(String message) {
        super(message);
    }

    public InvalidRequestException(String message, Throwable throwable){
        super(message, throwable);
    }

}

