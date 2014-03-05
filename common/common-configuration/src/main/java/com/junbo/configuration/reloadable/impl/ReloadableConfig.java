/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.configuration.reloadable.impl;

import com.junbo.configuration.ConfigService;
import com.junbo.configuration.impl.ConfigServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**.
 * Java doc for ReloadableConfig
 * @param <T>
 */
public abstract class ReloadableConfig<T> {
    //region private fields

    private static Logger logger = LoggerFactory.getLogger(ReloadableConfig.class);

    private ConfigService configService;
    private String key;
    private volatile String value;
    private volatile T parsedValue;

    //endregion

    public T get() {
        return parsedValue;
    }

    public String getRaw() {
        return value;
    }

    @Override
    public String toString() {
        return key + ": " + value;
    }

    //region private methods

    protected ReloadableConfig() {
    }

    protected void initialize(ConfigService configService, String key) {
        this.configService = configService;
        this.key = key;

        configService.addListener(key, new ConfigServiceImpl.ConfigListener() {
            @Override
            public void onConfigChanged(String key, String newValue) {
                ReloadableConfig.this.onConfigChanged(newValue);
            }
        });
        value = configService.getConfigValue(key);
        if (value == null) {
            throw new RuntimeException(String.format("Invalid ReloadableConfig key: [%s], the config is not found.",
                    key));
        }
        parsedValue = parseValue(value);
    }

    protected abstract T parseValue(String value);

    private void onConfigChanged(String newValue) {
        try {
            parsedValue = parseValue(newValue);
            value = newValue;
        }
        catch (Exception ex) {
            logger.warn(String.format("ReloadableConfig %s failed to parse new value: %s, using old value %s.",
                    key, newValue, value), ex);
        }
    }

    //endregion
}
