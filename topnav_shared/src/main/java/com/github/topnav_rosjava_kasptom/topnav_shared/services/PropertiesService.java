package com.github.topnav_rosjava_kasptom.topnav_shared.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

import static com.google.common.io.Resources.getResource;

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
            throw new RuntimeException("Call getInstance with the file path first");
        }
        return instance;
    }

    /**
     * @param configFilePath set to null to use the default config
     * @throws IOException if could not open the specified config file
     */
    private PropertiesService(String configFilePath) throws IOException {
        properties = new Properties();
        if (configFilePath != null) {
            properties.load(new FileInputStream(configFilePath));
        } else {
            String defaultConfigFilePath = Objects.requireNonNull(getResource("default.properties")).getPath();
            System.out.println("using the default config file");
            properties.load(new FileInputStream(defaultConfigFilePath));
        }
    }

    @Override
    public String getProperty(String propertyKey) {
        return properties.getProperty(propertyKey);
    }
}
