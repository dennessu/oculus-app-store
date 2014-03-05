/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.configuration.impl;

import com.junbo.configuration.ConfigContext;
import com.junbo.configuration.ConfigService;

import java.util.Properties;

/**
 * The ConfigServiceImpl instance for testing purpose.
 */
public class TestConfigServiceImpl implements ConfigService {
    private ConfigContext configContext;
    private Properties properties;

    public ConfigContext getConfigContext() {
        return configContext;
    }

    public String getConfigValue(String configKey) {
        return properties.getProperty(configKey);
    }

    public Properties getAllConfigItems() {
        return properties;
    }

    public void addListener(String configKey, ConfigListener listener) {
        // not supported
    }

    public void setConfigContext(ConfigContext configContext) {
        this.configContext = configContext;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
