/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.routing;

import com.junbo.common.routing.model.DataAccessAction;
import com.junbo.common.routing.model.DataAccessPolicy;
import com.junbo.common.routing.model.DataAccessPolicyConfigs;
import com.junbo.configuration.ConfigService;
import com.junbo.configuration.ConfigServiceManager;
import com.junbo.configuration.reloadable.StringConfig;
import com.junbo.configuration.reloadable.impl.ReloadableConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The policy for data access.
 */
public class DataAccessPolicies {
    private static final Logger logger = LoggerFactory.getLogger(DataAccessPolicies.class);

    private static DataAccessPolicies instance = new DataAccessPolicies();

    public static DataAccessPolicies instance() {
        return instance;
    }

    public static void setInstance(DataAccessPolicies daps) {
        DataAccessPolicies.instance = daps;
    }

    private StringConfig dataAccessPolicies;
    private StringConfig httpMethodActions;

    private DataAccessPolicyConfigs data;

    public DataAccessPolicies() {
        this.dataAccessPolicies = ReloadableConfigFactory.create("[common.routing.dataAccessPolicies]", StringConfig.class);
        this.httpMethodActions = ReloadableConfigFactory.create("[common.routing.httpMethodActions]", StringConfig.class);
        this.data = new DataAccessPolicyConfigs(dataAccessPolicies.get(), httpMethodActions.get());

        ConfigService.ConfigListener configListener = new ConfigService.ConfigListener() {
            @Override
            public void onConfigChanged(String configKey, String newValue) {
                reload();
            }
        };
        this.dataAccessPolicies.setConfigListener(configListener);
        this.httpMethodActions.setConfigListener(configListener);
    }


    public DataAccessPolicy getDataAccessPolicy(DataAccessAction action, Class<?> resource) {
        return data.getDataAccessPolicy(action, resource);
    }

    public DataAccessPolicy getHttpDataAccessPolicy(String httpMethod, Class<?> resource) {
        return data.getHttpDataAccessPolicy(httpMethod, resource);
    }

    private void reload() {
        try {
            DataAccessPolicyConfigs newData = new DataAccessPolicyConfigs(dataAccessPolicies.get(), httpMethodActions.get());
            this.data = newData;
        } catch (Exception ex) {
            logger.error("Failed to load new datacenter access policy configuration: \n" +
                    "\ndataAccessPolicies: " + this.dataAccessPolicies.get() +
                    "\nhttpMethodActions: " + this.httpMethodActions.get(), ex);
            // continue to use existing configuration.
        }
    }

    private ConfigService getConfigService() {
        return ConfigServiceManager.instance();
    }
}
