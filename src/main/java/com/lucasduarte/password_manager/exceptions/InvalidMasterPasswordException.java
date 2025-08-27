package com.lucasduarte.password_manager.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class InvalidMasterPasswordException extends RuntimeException {
    public InvalidMasterPasswordException(String message) {
        super(message);
    }
}
