package com.springapi.springapitechnicaltest.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Value Not Found")
public class NotFoundException extends ResponseStatusException {
    public NotFoundException(String message){
        super(HttpStatus.NOT_FOUND, message);
    }

}