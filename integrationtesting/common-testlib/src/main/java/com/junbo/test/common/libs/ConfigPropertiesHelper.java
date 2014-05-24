/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.libs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

/**
 * @author Jason
 * Time: 3/17/204
 * For reading test config file
 */
public final class ConfigPropertiesHelper {

    private static LogHelper logger =  new LogHelper(ConfigPropertiesHelper.class);
    private static ConfigPropertiesHelper instance;

    private ConfigPropertiesHelper() {}

    public static synchronized ConfigPropertiesHelper instance() {
        if (instance == null) {
            instance = new ConfigPropertiesHelper();
            initProperties();
        }
        return instance;
    }

    /**
     * Gets a property value.
     * @param propertyName
     * @return
     */
    public String getProperty(String propertyName) {
        String prop = System.getProperty(propertyName);
        if(prop == null) {
            throw new IllegalArgumentException("No system property for key " + propertyName);
        }
        return prop;
    }

    /**
     * Gets a property, returns defaultValue if no property found.
     * @param propertyName
     * @param defaultValue
     * @return
     */
    public String getProperty(String propertyName, String defaultValue) {
        return System.getProperty(propertyName, defaultValue);
    }

    private static void initProperties() {
        String config = System.getProperty("test.config");
        if (config != null && config.length() > 0) {
            logger.logInfo("test config: " + config);
            loadTestProperties(String.format("%s.properties", config));
        }
        else {
            loadTestProperties("testConfig.properties");
        }
    }

    private static void loadTestProperties(String path) {
        Properties props = new Properties();
        try {
            InputStream io = instance.getClass().getClassLoader().getResourceAsStream(path);
            if (io != null) {
                props.load(io);
            }
            else {
                throw new RuntimeException("Could not find the '" + path + "' properties file in the " +
                        "resource directory.");
            }
        } catch(IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        logger.logInfo("**** Setting system properties from " + path + ":");
        Enumeration<?> propertyNames = props.propertyNames();
        while (propertyNames.hasMoreElements()) {
            String key = (String) propertyNames.nextElement();
            String value = props.getProperty(key);
            if (!System.getProperties().containsKey(key)) {
                System.setProperty(key, value);
                logger.logInfo(key + ": " + System.getProperty(key));
            }
            else {
                logger.logInfo("ALREADY SET - " + key + ": " + System.getProperty(key));
            }
        }
        logger.logInfo("**** Finished loading " + path);
    }

}
