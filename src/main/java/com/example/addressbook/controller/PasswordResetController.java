package com.example.addressbook.controller;

import com.example.addressbook.dto.ChangePasswordDto;
import com.example.addressbook.dto.PasswordResetDto;
import com.example.addressbook.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Tag(name = "Password Reset")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/auth/forgot-password")
    @Operation(summary = "Initiate password reset")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> request) {
        String usernameOrEmail = request.get("usernameOrEmail");
        passwordResetService.initiatePasswordReset(usernameOrEmail);
        return ResponseEntity.ok("Password reset email sent!");
    }

    @PostMapping("/auth/reset-password")
    @Operation(summary = "Reset password")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetDto passwordResetDto) {
        passwordResetService.resetPassword(passwordResetDto);
        return ResponseEntity.ok("Password reset successfully!");
    }

    @PostMapping("/auth/change-password")
    @Operation(summary = "Change password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Get logged-in username

        if (passwordResetService.changePassword(username, changePasswordDto)) {
            return ResponseEntity.ok("Password changed successfully!");
        } else {
            return ResponseEntity.badRequest().body("Failed to change password. Please check your current password.");
        }
    }
}