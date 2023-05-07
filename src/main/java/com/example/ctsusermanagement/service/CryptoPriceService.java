package com.example.ctsusermanagement.service;

import java.io.IOException;

public interface CryptoPriceService {

    Double fetchCryptoPrice(String symbol) throws IOException;
}
