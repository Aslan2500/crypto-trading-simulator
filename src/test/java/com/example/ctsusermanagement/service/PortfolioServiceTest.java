package com.example.ctsusermanagement.service;

import com.example.ctsusermanagement.model.Portfolio;
import com.example.ctsusermanagement.model.Position;
import com.example.ctsusermanagement.model.User;
import com.example.ctsusermanagement.repository.PortfolioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class PortfolioServiceTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private UserService userService;

    @Mock
    private CryptoPriceService cryptoPriceService;

    private PortfolioServiceImpl portfolioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        portfolioService = new PortfolioServiceImpl(portfolioRepository, userService, cryptoPriceService);
    }

    @Test
    void deposit_ShouldUpdateCashBalanceAndTotalValue() {
        User user = new User();
        Portfolio portfolio = new Portfolio();
        portfolio.setCashBalance(100.0);
        portfolio.setTotalValue(200.0);
        user.setPortfolio(portfolio);

        when(userService.getCurrentUser()).thenReturn(user);
        when(portfolioRepository.save(portfolio)).thenReturn(portfolio);

        double depositAmount = 50.0;
        portfolioService.deposit(depositAmount);

        assertEquals(150.0, portfolio.getCashBalance());
        assertEquals(250.0, portfolio.getTotalValue());

        verify(portfolioRepository, times(1)).save(portfolio);
    }

    @Test
    void updateTotalValue_ShouldReturnCurrentTotalValue_WhenPositionListIsEmpty() {
        User user = new User();
        Portfolio portfolio = new Portfolio();
        portfolio.setTotalValue(500.0);
        portfolio.setPositions(new ArrayList<>());
        user.setPortfolio(portfolio);

        when(userService.getCurrentUser()).thenReturn(user);
        double totalValue = portfolioService.updateTotalValue();

        assertEquals(500.0, totalValue);
        verify(portfolioRepository, never()).save(portfolio);
    }

    @Test
    void updateTotalValue_ShouldCalculateTotalValue_WhenPositionListIsNotEmpty() throws IOException {
        User user = new User();
        Portfolio portfolio = new Portfolio();
        portfolio.setCashBalance(100.0);
        portfolio.setTotalValue(200.0);

        List<Position> positionList = new ArrayList<>();
        Position position1 = new Position();
        position1.setTicket("BTC");
        position1.setQuantity(2.0);
        positionList.add(position1);

        Position position2 = new Position();
        position2.setTicket("ETH");
        position2.setQuantity(3.0);
        positionList.add(position2);

        portfolio.setPositions(positionList);
        user.setPortfolio(portfolio);

        when(userService.getCurrentUser()).thenReturn(user);

        when(cryptoPriceService.fetchCryptoPrice("BTC")).thenReturn(50000.0);
        when(cryptoPriceService.fetchCryptoPrice("ETH")).thenReturn(3000.0);

        when(portfolioRepository.save(portfolio)).thenReturn(portfolio);

        double totalValue = portfolioService.updateTotalValue();

        double expectedTotalValue = 100.0 + (2.0 * 50000.0) + (3.0 * 3000.0);
        assertEquals(expectedTotalValue, totalValue);

        assertEquals(expectedTotalValue, portfolio.getTotalValue());

        verify(portfolioRepository, times(1)).save(portfolio);
    }
}
