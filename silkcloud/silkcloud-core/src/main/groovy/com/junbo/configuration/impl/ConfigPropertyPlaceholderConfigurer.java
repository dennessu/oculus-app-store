/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.configuration.impl;

import com.junbo.configuration.ConfigServiceManager;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * Java doc for ConfigPropertyPlaceholderConfigurer.
 */
public class ConfigPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
    @Override
    protected String resolveSystemProperty(String key) {
        String propVal = ConfigServiceManager.instance().getConfigValue(key);
        if (propVal == null) {
            propVal = super.resolveSystemProperty(key);
        }
        return propVal;
    }
}
