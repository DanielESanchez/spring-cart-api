package com.springapi.springapitechnicaltest.controllers;

import com.springapi.springapitechnicaltest.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/${api.request.path}")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    @PatchMapping("/user/enable/{username}")
    public ResponseEntity<?> enableUser(@PathVariable String username){
        adminService.enableUser(username);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/user/disable/{username}")
    public ResponseEntity<?> disableUser(@PathVariable String username){
        adminService.disableUser(username);
        return ResponseEntity.noContent().build();
    }
}
