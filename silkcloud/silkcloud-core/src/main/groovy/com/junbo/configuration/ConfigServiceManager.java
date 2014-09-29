/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.configuration;

import com.junbo.configuration.impl.ConfigServiceImpl;
import org.springframework.beans.factory.annotation.Required;

/**
 * Provide singleton access to config service.
 */
public class ConfigServiceManager {

    private static ConfigService configService = new ConfigServiceImpl();

    public static ConfigService instance() {
        return configService;
    }

    public static void setInstance(ConfigService configService) {
        ConfigServiceManager.configService = configService;
    }

    @Required
    public void setConfigService(ConfigService configService) {
        setInstance(configService);
    }
}
