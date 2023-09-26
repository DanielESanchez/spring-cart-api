package com.springapi.springapitechnicaltest.controllers;

import com.springapi.springapitechnicaltest.services.UserService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.request.path}")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@ApiResponses( value = {
        @ApiResponse(responseCode = "400", description = "Missing required fields or set types different than required",
                content = { @Content(schema = @Schema) }),
        @ApiResponse(responseCode = "403", description = "You do not have permission to do this", content = { @Content(schema = @Schema) })
})
public class UserController {

    private final UserService userService;

    @ApiResponses( value = {
            @ApiResponse(responseCode = "204",
                    description = "User marked as enabled", content = { @Content(schema = @Schema) }),
            @ApiResponse(responseCode = "404", description = "User not found", content = { @Content(schema = @Schema) })
    })
    @PatchMapping("/admin/enable/user/{username}")
    public ResponseEntity<?> enableUser(@PathVariable String username){
        userService.enableUser(username);
        return ResponseEntity.noContent().build();
    }

    @ApiResponses( value = {
            @ApiResponse(responseCode = "204",
                    description = "User marked as disabled", content = { @Content(schema = @Schema) }),
            @ApiResponse(responseCode = "404", description = "User not found", content = { @Content(schema = @Schema) })
    })
    @PatchMapping("/admin/disable/user/{username}")
    public ResponseEntity<?> disableUser(@PathVariable String username){
        userService.disableUser(username);
        return ResponseEntity.noContent().build();
    }
}
