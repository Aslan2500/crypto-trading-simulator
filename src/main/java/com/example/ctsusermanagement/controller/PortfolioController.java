package com.example.ctsusermanagement.controller;

import com.example.ctsusermanagement.service.PortfolioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Portfolio", description = "Portfolio API")
@RestController
@RequestMapping("/api/v1/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @SecurityRequirement(name = "TOKEN")
    @Operation(summary = "Deposit")
    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(Double amount) {
        portfolioService.deposit(amount);
        return ResponseEntity.ok("Deposit successfully accepted");
    }
}
