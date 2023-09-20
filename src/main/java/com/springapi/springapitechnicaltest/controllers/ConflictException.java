package com.springapi.springapitechnicaltest.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "There are data that could not be validated")
public class ConflictException extends ResponseStatusException {

    public ConflictException(String message){
        super(HttpStatus.CONFLICT, message);
    }
}
