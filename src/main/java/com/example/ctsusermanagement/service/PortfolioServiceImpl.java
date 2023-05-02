package com.example.ctsusermanagement.service;

import com.example.ctsusermanagement.repository.PortfolioRepository;
import com.example.ctsusermanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;

    @Override
    public void deposit(BigDecimal deposit) { // TODO: Добавить историю транзакций
        var user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var portfolio = user.getPortfolio();
        portfolio.setCashBalance(deposit);
        portfolio.setTotalValue(portfolio.getTotalValue().add(deposit));
        portfolioRepository.save(portfolio);
    }
}
