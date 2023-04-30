package com.example.ctsusermanagement.service;

import java.math.BigDecimal;

public interface PortfolioService {

    void deposit(Long userId, BigDecimal deposit);
}
