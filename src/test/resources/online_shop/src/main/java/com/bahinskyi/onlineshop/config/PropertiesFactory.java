package com.bahinskyi.onlineshop.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesFactory {
    private String path;

    public PropertiesFactory(String path) {
        this.path = path;
    }

    public Properties getProperties() {
        if ("PROD".equalsIgnoreCase(System.getenv("env"))) {
            return getProdProperties();
        }
        return getDevProperties();
    }

    private Properties getDevProperties() {
        Properties properties = new Properties();
        try (InputStream propertyStream = PropertiesFactory.class.getClassLoader().getResourceAsStream(path)) {
            properties.load(propertyStream);
        } catch (IOException e) {
            throw new RuntimeException("Get prod properties error", e);
        }

        return properties;
    }

    private Properties getProdProperties() {
        Properties properties = new Properties();
        String url = System.getenv("JDBC_DATABASE_URL");
        properties.setProperty("jdbc.url", url);
        properties.setProperty("port", String.valueOf(getPort()));

        return properties;
    }

    private int getPort() {
        String herokuPort = System.getenv("PORT");
        return Integer.parseInt(herokuPort);
    }
}
