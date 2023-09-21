package com.springapi.springapitechnicaltest.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "You have no permission to access this")
public class ForbiddenException extends ResponseStatusException {
    public ForbiddenException(String message){
        super(HttpStatus.FORBIDDEN, message);
    }
}
