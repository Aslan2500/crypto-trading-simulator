package com.example.ctsusermanagement.service;

public interface TradeService {

    void buy(String ticket, Double amountToSpend);
    void sell(String ticket, Double amountCryptoToSell);
}
