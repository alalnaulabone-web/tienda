package com.tienda.alal.exception;

public class SpExecutionException extends RuntimeException {
    public SpExecutionException(String message) {
        super(message);
    }

    public SpExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
