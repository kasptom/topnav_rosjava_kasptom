package com.github.topnav_rosjava_kasptom.services;

import sun.plugin.dom.exception.InvalidStateException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class PropertiesService implements IPropertiesService {
    private static IPropertiesService instance;
    private final Properties properties;

    /**
     * @param configFilePath pass null to use the default config file path
     * @return singleton instance of the service
     * @throws IOException if the file path is invalid
     */
    public static IPropertiesService getInstance(String configFilePath) throws IOException {
        if (instance == null) {
            instance = new PropertiesService(configFilePath);
        }
        return instance;
    }

    public static IPropertiesService getInstance() {
        if (instance == null) {
            throw new InvalidStateException("Call getInstance with the file path first");
        }
        return instance;
    }

    private PropertiesService(String configFilePath) throws IOException {
        properties = new Properties();
        if (configFilePath != null) {
            properties.load(new FileInputStream(configFilePath));
        } else {
            String deraultConfigFilePath = Objects.requireNonNull(PropertiesService.class.getClassLoader().getResource("default.properties")).getPath();
            System.out.println("using default config file");
            properties.load(new FileInputStream(deraultConfigFilePath));
        }
    }

    @Override
    public String getProperty(String propertyKey) {
        return properties.getProperty(propertyKey);
    }
}
