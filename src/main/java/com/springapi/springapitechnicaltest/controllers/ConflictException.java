package com.springapi.springapitechnicaltest.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Value Not Found")
public class ConflictException extends RuntimeException{

    public ConflictException(){}
}
