/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.configuration.reloadable.impl;

/**.
 * Java doc
 */
public class ReloadableConfigFactory {
    private static final String CONFIG_PREFIX = "[";
    private static final String CONFIG_SUFFIX = "]";

    private ReloadableConfigFactory() {}

    public static <T extends ReloadableConfig> T create(String key, Class<T> targetType) {
        key = trimBrackets(key);
        try {
            T instance = targetType.newInstance();
            instance.initialize(key);
            return instance;
        }
        catch (Exception ex) {
            throw new RuntimeException("Error creating instance of " + targetType, ex);
        }
    }

    private static String trimBrackets(String key) {
        if (!key.startsWith(CONFIG_PREFIX) || !key.endsWith(CONFIG_SUFFIX) || key.length() <= 2) {
            throw new RuntimeException("Invalid ReloadableConfig key: " + key);
        }
        return key.substring(1, key.length() - 1);
    }
}
