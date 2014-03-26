<<<<<<< HEAD:integrationtesting/integration-common/src/main/java/com/junbo/testing/common/libs/ConfigPropertiesHelper.java
/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.testing.common.libs;

import java.io.IOException;
import java.io.InputStream;
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
            init();
        }
        return instance;
    }

    /**
     * Gets a property value from key propertyName.  Throws IllegalArgumentException if property not found
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
     * Gets a property with key propertyName.  Returns defaultValue if no property found.
     * @param propertyName
     * @param defaultValue
     * @return
     */
    public String getProperty(String propertyName, String defaultValue) {
        return System.getProperty(propertyName, defaultValue);
    }

    private static void init() {
        String config = System.getProperty("test.config");
        if (config != null && config.length() > 0 && !config.startsWith("${")) {
            logger.logInfo("universe: " + config);
            loadTestProperties(String.format("testconfig/%s.config", config));
        }
        else {
            loadTestProperties("testconfig/default.config");
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
                        "resource directory, as specified in test.config.");
            }
        } catch(IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        logger.logInfo("**** Setting system properties from " + path + ":");
        for (Object key : props.keySet()) {
            if (!System.getProperties().containsKey(key) ||
                    System.getProperty((String)key).startsWith("${")) {
                System.setProperty((String)key, props.getProperty((String)key));
                logger.logInfo(key + ": " + System.getProperty((String)key));
            } else {
                logger.logInfo("ALREADY SET - " + key + ": " + System.getProperty((String)key));
            }
        }
        logger.logInfo("**** Finished loading " + path);
    }

}
=======
/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.libs;

import java.io.IOException;
import java.io.InputStream;
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
            init();
        }
        return instance;
    }

    /**
     * Gets a property value from key propertyName.  Throws IllegalArgumentException if property not found
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
     * Gets a property with key propertyName.  Returns defaultValue if no property found.
     * @param propertyName
     * @param defaultValue
     * @return
     */
    public String getProperty(String propertyName, String defaultValue) {
        return System.getProperty(propertyName, defaultValue);
    }

    private static void init() {
        String config = System.getProperty("test.config");
        if (config != null && !config.startsWith("${")) {
            logger.logInfo("universe: " + config);
            loadTestProperties(String.format("testconfig/%s.config", config));
        }
        loadTestProperties("testconfig/default.config");
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
                        "resource directory, as specified in test.config.");
            }
        } catch(IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        logger.logInfo("**** Setting system properties from " + path + ":");
        for (Object key : props.keySet()) {
            if (!System.getProperties().containsKey(key) ||
                    System.getProperty((String)key).startsWith("${")) {
                System.setProperty((String)key, props.getProperty((String)key));
                logger.logInfo(key + ": " + System.getProperty((String)key));
            } else {
                logger.logInfo("ALREADY SET - " + key + ": " + System.getProperty((String)key));
            }
        }
        logger.logInfo("**** Finished loading " + path);
    }

}
>>>>>>> upstream/master:integrationtesting/integration-common/src/main/java/com/junbo/test/common/libs/ConfigPropertiesHelper.java
