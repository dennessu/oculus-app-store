/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.configuration.impl;

import com.junbo.configuration.ConfigService;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * Java doc for ConfigPropertyPlaceholderConfigurer.
 */
public class ConfigPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

    private ConfigService configService;

    @Required
    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    @Override
    protected String resolveSystemProperty(String key) {
        String propVal = configService.getConfigValue(key);
        if (propVal == null) {
            propVal = super.resolveSystemProperty(key);
        }
        return propVal;
    }
}
