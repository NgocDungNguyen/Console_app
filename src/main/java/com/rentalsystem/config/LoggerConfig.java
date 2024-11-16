package com.rentalsystem.config;

import java.io.IOException;
import java.util.logging.*;

public class LoggerConfig {
    private static final Logger LOGGER = Logger.getLogger(LoggerConfig.class.getName());

    static {
        try {
            FileHandler fileHandler = new FileHandler("rental_system.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            LOGGER.severe("Failed to initialize logger: " + e.getMessage());
        }
    }

    public static Logger getLogger() {
        return LOGGER;
    }
}