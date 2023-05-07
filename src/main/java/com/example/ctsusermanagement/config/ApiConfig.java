package com.example.ctsusermanagement.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApiConfig {

    private static final String CONFIG_FILE_NAME = "config.properties";

    public static String getApiKey() {
        Properties properties = new Properties();
        try (InputStream inputStream = ApiConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + CONFIG_FILE_NAME);
            }
            properties.load(inputStream);
            return properties.getProperty("coinmarketcap.api.key");
        } catch (IOException e) {
            throw new RuntimeException("Error loading configuration file", e);
        }
    }
}
