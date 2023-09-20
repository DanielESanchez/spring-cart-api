package com.springapi.springapitechnicaltest.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "There are empty or invalid fields on the information received")
public class BadRequestException extends ResponseStatusException {
    public BadRequestException(String message){
        super(HttpStatus.BAD_REQUEST, message);
    }
}
