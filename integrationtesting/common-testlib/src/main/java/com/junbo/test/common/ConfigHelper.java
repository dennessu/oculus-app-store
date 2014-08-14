/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.*;
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

            if (getSetting("TestEncrypted") != null &&
                    getSetting("TestEncrypted").equalsIgnoreCase("true")) {
                if (getSetting("TestClient") != null && getSetting("TestClient").length() > 0 ) {
                    ReadTestClientNameFromFile(getSetting("TestClient"));
                }
                else {
                    Assert.fail("no TestClient property defined or null value for it in PPE/PRDO environment");
                }
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static String getSetting(String key) {
        return properties.getProperty(key);
    }

    private static void ReadTestClientNameFromFile(String path) {
        try {
            //opening file for reading
            FileInputStream file = new FileInputStream(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(file));

            //reading file content line by line
            String line = reader.readLine();
            properties.put("TestClient", line);
        } catch (FileNotFoundException ex) {
            Assert.fail(ex.toString());
        } catch (IOException ex) {
            Assert.fail(ex.toString());
        } catch (NullPointerException ex) {
            Assert.fail(ex.toString());
        }

    }
}
