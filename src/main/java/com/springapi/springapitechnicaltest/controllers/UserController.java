package com.springapi.springapitechnicaltest.controllers;

import com.springapi.springapitechnicaltest.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.request.path}")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    @PatchMapping("/admin/enable/user/{username}")
    public ResponseEntity<?> enableUser(@PathVariable String username){
        userService.enableUser(username);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/admin/disable/user/{username}")
    public ResponseEntity<?> disableUser(@PathVariable String username){
        userService.disableUser(username);
        return ResponseEntity.noContent().build();
    }
}