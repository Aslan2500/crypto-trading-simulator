package com.example.ctsusermanagement.service;

import com.example.ctsusermanagement.exception.NotEnoughCryptoException;
import com.example.ctsusermanagement.exception.NotEnoughMoneyException;
import com.example.ctsusermanagement.exception.PositionNotFoundException;
import com.example.ctsusermanagement.model.Portfolio;
import com.example.ctsusermanagement.model.Position;
import com.example.ctsusermanagement.model.User;
import com.example.ctsusermanagement.repository.PortfolioRepository;
import com.example.ctsusermanagement.repository.PositionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TradeServiceTest {

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private CryptoPriceService cryptoPriceService;

    @Mock
    private UserService userService;

    private TradeServiceImpl tradeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tradeService = new TradeServiceImpl(positionRepository, portfolioRepository, cryptoPriceService, userService);
    }

    @Test
    void buy_ShouldThrowNotEnoughMoneyException_WhenTotalValueIsLessThanAmountToSpend() {
        User user = new User();
        Portfolio portfolio = new Portfolio();
        portfolio.setTotalValue(300.0);
        portfolio.setCashBalance(300.0);
        portfolio.setPositions(new ArrayList<>());
        user.setPortfolio(portfolio);

        when(userService.getCurrentUser()).thenReturn(user);

        String ticket = "BTC";
        Double amountToSpend = 400.0;

        assertThrows(NotEnoughMoneyException.class, () -> {
            tradeService.buy(ticket, amountToSpend);
        });

        verify(positionRepository, never()).save(any(Position.class));
        verify(portfolioRepository, never()).save(any(Portfolio.class));
    }

    @Test
    void buy_ShouldAddNewPositionAndDeductCashBalance_WhenPositionDoesNotExist() throws IOException {
        User user = new User();
        Portfolio portfolio = new Portfolio();
        portfolio.setTotalValue(300.0);
        portfolio.setCashBalance(300.0);
        portfolio.setPositions(new ArrayList<>());
        user.setPortfolio(portfolio);
        when(userService.getCurrentUser()).thenReturn(user);

        Double cryptoPrice = 100.0;
        when(cryptoPriceService.fetchCryptoPrice("BTC")).thenReturn(cryptoPrice);

        when(positionRepository.save(any(Position.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(portfolioRepository.save(any(Portfolio.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String ticket = "BTC";
        Double amountToSpend = 200.0;
        tradeService.buy(ticket, amountToSpend);

        verify(positionRepository, times(1)).save(any(Position.class));

        assertEquals(100.0, portfolio.getCashBalance());
    }

    @Test
    void buy_ShouldUpdateExistingPositionAndDeductCashBalance_WhenPositionExists() throws IOException {
        User user = new User();
        Portfolio portfolio = new Portfolio();
        portfolio.setTotalValue(500.0);
        portfolio.setCashBalance(300.0);
        user.setPortfolio(portfolio);
        when(userService.getCurrentUser()).thenReturn(user);

        Double cryptoPrice = 100.0;
        when(cryptoPriceService.fetchCryptoPrice("BTC")).thenReturn(cryptoPrice);

        List<Position> positions = new ArrayList<>();
        Position existingPosition = new Position();
        existingPosition.setTicket("BTC");
        existingPosition.setQuantity(2.0);
        existingPosition.setAveragePrice(150.0);
        positions.add(existingPosition);
        portfolio.setPositions(positions);

        when(positionRepository.save(any(Position.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(portfolioRepository.save(any(Portfolio.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String ticket = "BTC";
        Double amountToSpend = 200.0;
        tradeService.buy(ticket, amountToSpend);

        verify(positionRepository, times(1)).save(existingPosition);

        assertEquals(100.0, portfolio.getCashBalance());
    }

    @Test
    void sell_ShouldThrowPositionNotFoundException_WhenPositionDoesNotExist() {
        User user = new User();
        Portfolio portfolio = new Portfolio();
        List<Position> positions = new ArrayList<>();
        portfolio.setPositions(positions);
        user.setPortfolio(portfolio);
        when(userService.getCurrentUser()).thenReturn(user);

        String ticket = "BTC";
        Double amountCryptoToSell = 2.0;
        assertThrows(PositionNotFoundException.class, () -> tradeService.sell(ticket, amountCryptoToSell));

        verify(positionRepository, never()).save(any(Position.class));
    }

    @Test
    void sell_ShouldThrowNotEnoughCryptoException_WhenQuantityIsLessThanAmountCryptoToSell() {
        User user = new User();
        Portfolio portfolio = new Portfolio();
        List<Position> positions = new ArrayList<>();
        Position position = new Position();
        position.setTicket("BTC");
        position.setQuantity(2.0);
        positions.add(position);
        portfolio.setPositions(positions);
        user.setPortfolio(portfolio);
        when(userService.getCurrentUser()).thenReturn(user);

        String ticket = "BTC";
        Double amountCryptoToSell = 3.0;
        assertThrows(NotEnoughCryptoException.class, () -> tradeService.sell(ticket, amountCryptoToSell));

        verify(positionRepository, never()).save(any(Position.class));
    }

    @Test
    void sell_ShouldUpdateExistingPositionAndAddToCashBalance_WhenPositionExists() throws IOException {
        User user = new User();
        Portfolio portfolio = new Portfolio();
        portfolio.setCashBalance(1000.0);
        List<Position> positions = new ArrayList<>();
        Position position = new Position();
        position.setTicket("BTC");
        position.setQuantity(5.0);
        positions.add(position);
        portfolio.setPositions(positions);
        user.setPortfolio(portfolio);
        when(userService.getCurrentUser()).thenReturn(user);

        Double cryptoPrice = 200.0;
        when(cryptoPriceService.fetchCryptoPrice("BTC")).thenReturn(cryptoPrice);

        when(positionRepository.save(any(Position.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(portfolioRepository.save(any(Portfolio.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String ticket = "BTC";
        Double amountCryptoToSell = 3.0;
        tradeService.sell(ticket, amountCryptoToSell);

        verify(positionRepository, times(1)).save(position);

        assertEquals(1600.0, portfolio.getCashBalance());
    }
}
