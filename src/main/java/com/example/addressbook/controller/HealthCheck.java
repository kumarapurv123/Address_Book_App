package com.example.addressbook.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@Tag(name = "Health Check")
public class HealthCheck {
    @RequestMapping("health-check")
    @Operation(summary = "Health Check")
    public String healthCheck() {
        return "{\"message\":\"Health Check:Good\"}";
    }

    @RequestMapping("public/health-check")
    @Operation(summary = "Public Health Check")
    public String publicHealthCheck() {
        return "{\"message\":\"Public Health Check:Good\"}";
    }
}