/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common;

import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Properties;

/**
 * @author dw
 */
public final class ConfigHelper {
    private static final Properties properties = new Properties();

    static {
        try {
            //ClassLoader loader = Thread.currentThread().getContextClassLoader();
            ClassLoader loader = Test.class.getClassLoader();
            String configFile = "testConfig.properties";
            String profile = System.getProperty("profile", "test");
            if (!profile.equals("test") && !profile.equals("local") && !profile.trim().isEmpty()) {
                configFile = profile + ".properties";
            }
            properties.load(loader.getResourceAsStream(configFile));
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static String getSetting(String key) {
        return properties.getProperty(key);
    }
}
