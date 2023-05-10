package com.example.ctsusermanagement.service;

import com.example.ctsusermanagement.exception.CryptoPriceFetchException;
import com.example.ctsusermanagement.model.Position;
import com.example.ctsusermanagement.repository.PortfolioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final UserService userService;
    private final CryptoPriceService cryptoPriceService;

    @Override
    @Transactional
    public void deposit(Double deposit) { // TODO: Добавить историю транзакций
        var user = userService.getCurrentUser();
        var portfolio = user.getPortfolio();
        portfolio.setCashBalance(portfolio.getCashBalance() + deposit);
        portfolio.setTotalValue(portfolio.getTotalValue() + deposit);
        portfolioRepository.save(portfolio);
    }

    @Override
    @Transactional
    public Double updateTotalValue() { //TODO: Переделать
        var user = userService.getCurrentUser();
        var portfolio = user.getPortfolio();
        List<Position> positionList = portfolio.getPositions();
        if (positionList.isEmpty()) {
            return portfolio.getTotalValue();
        }
        Double totalValue = portfolio.getCashBalance();
        for (Position p : positionList) {
            String ticket = p.getTicket();
            Double cryptoPrice = getCryptoPrice(ticket);
            Double quantity = p.getQuantity();
            totalValue += cryptoPrice * quantity;
        }
        portfolio.setTotalValue(totalValue);
        portfolioRepository.save(portfolio);
        return totalValue;
    }

    private Double getCryptoPrice(String ticket) {
        try {
            return cryptoPriceService.fetchCryptoPrice(ticket);
        } catch (IOException e) {
            throw new CryptoPriceFetchException("Error fetching the crypto price for ticket: " + ticket, e);
        }
    }
}
