package com.example.ctsusermanagement.service;

import com.example.ctsusermanagement.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final UserService userService;

    @Override
    public void deposit(Double deposit) { // TODO: Добавить историю транзакций
        var user = userService.getCurrentUser();
        var portfolio = user.getPortfolio();
        portfolio.setCashBalance(portfolio.getCashBalance() + deposit);
        portfolio.setTotalValue(portfolio.getTotalValue() + deposit);
        portfolioRepository.save(portfolio);
    }
}
