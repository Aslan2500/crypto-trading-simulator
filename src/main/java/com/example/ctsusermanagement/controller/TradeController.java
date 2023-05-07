package com.example.ctsusermanagement.controller;

import com.example.ctsusermanagement.service.TradeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trade")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    @SecurityRequirement(name = "TOKEN")
    @PostMapping("/{ticket}/buy")
    public ResponseEntity<String> buy(@PathVariable("ticket") String ticket, Double amountToSpend) {
        tradeService.buy(ticket, amountToSpend);
        return ResponseEntity.ok("response");
    }
}
