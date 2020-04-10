package com.bahinskyi.onlineshop.exception;

public class LoginPasswordInvalidException extends RuntimeException {
    public LoginPasswordInvalidException(String message) {
        super(message);
    }
}
