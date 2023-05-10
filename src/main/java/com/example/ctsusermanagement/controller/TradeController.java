package com.example.ctsusermanagement.controller;

import com.example.ctsusermanagement.service.TradeService;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "Buy crypto")
    @PostMapping("/{ticket}/buy")
    public ResponseEntity<String> buy(@PathVariable("ticket") String ticket, Double amountToSpend) {
        tradeService.buy(ticket, amountToSpend);
        return ResponseEntity.ok("Operation succeeded");
    }

    @SecurityRequirement(name = "TOKEN")
    @Operation(summary = "Sell crypto")
    @PostMapping("/{ticket}/sell")
    public ResponseEntity<String> sell(@PathVariable("ticket") String ticket, Double amountCryptoToSell) {
        tradeService.sell(ticket, amountCryptoToSell);
        return ResponseEntity.ok("response");
    }
}
