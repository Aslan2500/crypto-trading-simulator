package com.example.ctsusermanagement.service;

import com.example.ctsusermanagement.exception.CryptoPriceFetchException;
import com.example.ctsusermanagement.exception.NotEnoughMoneyException;
import com.example.ctsusermanagement.model.Position;
import com.example.ctsusermanagement.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TradeServiceImpl implements TradeService {

    private final PositionRepository positionRepository;
    private final CryptoPriceService cryptoPriceService;
    private final UserService userService;

    @Override
    public void buy(String ticket, Double amountToSpend) {
        var user = userService.getCurrentUser();
        var portfolio = user.getPortfolio();
        Double totalValue = portfolio.getTotalValue();

        if (totalValue.compareTo(amountToSpend) < 0) {
            throw new NotEnoughMoneyException("You don't have enough money");
        }

        List<Position> positions = portfolio.getPositions();
        Position position = checkIfUserAlreadyHasThisPosition(ticket, positions);

        if (position == null) {
            positions.add(Position
                    .builder()
                    .ticket(ticket)
                    .averagePrice((double) 0)
                    .quantity((double) 0)
                    .portfolio(portfolio)
                    .build());
            position = checkIfUserAlreadyHasThisPosition(ticket, positions);
        }

        Double price = getCryptoPrice(ticket);
        Double purchaseQuantity = amountToSpend / price;
        addPurchase(purchaseQuantity, price, position);
        portfolio.setCashBalance(portfolio.getCashBalance() - amountToSpend);
        positionRepository.save(position);
    }

    private Double getCryptoPrice(String ticket) {
        try {
            return cryptoPriceService.fetchCryptoPrice(ticket);
        } catch (IOException e) {
            throw new CryptoPriceFetchException("Error fetching the crypto price for ticket: " + ticket, e);
        }
    }

    private Position checkIfUserAlreadyHasThisPosition(String ticket, List<Position> positions) {
        Optional<Position> result = positions.stream()
                .filter(position -> position.getTicket().equals(ticket))
                .findFirst();
        return result.orElse(null);
    }

    private void addPurchase(Double purchaseQuantity, Double purchasePrice, Position position) {
        Double quantity = position.getQuantity();
        Double price = position.getAveragePrice();

        Double totalCost = (price * quantity) + (purchaseQuantity * purchasePrice);

        position.setQuantity(position.getQuantity() + purchaseQuantity);

        position.setAveragePrice(totalCost / position.getQuantity());
    }
}
