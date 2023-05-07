package com.example.ctsusermanagement.service;

import com.example.ctsusermanagement.config.ApiConfig;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class CryptoPriceServiceImpl implements CryptoPriceService {


    private static final String COINMARKETCAP_API_BASE_URL = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest";
    private static final String API_KEY = ApiConfig.getApiKey();

    public Double fetchCryptoPrice(String symbol) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(COINMARKETCAP_API_BASE_URL + "?symbol=" + symbol)
                .addHeader("X-CMC_PRO_API_KEY", API_KEY)
                .addHeader("Accept", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response.code());
            }

            assert response.body() != null;
            String jsonResponse = response.body().string();
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);

            JsonObject dataObject = jsonObject.getAsJsonObject("data");
            JsonObject symbolObject = dataObject.getAsJsonObject(symbol);
            JsonObject quoteObject = symbolObject.getAsJsonObject("quote");
            JsonObject usdObject = quoteObject.getAsJsonObject("USD");

            return usdObject.get("price").getAsDouble();
        }
    }
}
