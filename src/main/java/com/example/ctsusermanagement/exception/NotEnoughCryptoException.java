package com.example.ctsusermanagement.exception;

public class NotEnoughCryptoException extends RuntimeException {

    public NotEnoughCryptoException(String message) {
        super(message);
    }

    public NotEnoughCryptoException(String message, Throwable cause) {
        super(message, cause);
    }
}
