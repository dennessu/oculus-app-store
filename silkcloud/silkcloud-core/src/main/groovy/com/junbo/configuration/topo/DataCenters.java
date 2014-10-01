/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.configuration.topo;

import com.junbo.configuration.ConfigService;
import com.junbo.configuration.ConfigServiceManager;
import com.junbo.configuration.reloadable.StringConfig;
import com.junbo.configuration.reloadable.impl.ReloadableConfigFactory;
import com.junbo.configuration.topo.model.DataCenter;
import com.junbo.configuration.topo.model.DataCentersConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * DataCenters.
 *
 * This is a global setting.
 */
public class DataCenters {
    private static final Logger logger = LoggerFactory.getLogger(DataCenters.class);

    private static DataCenters instance = new DataCenters();

    public static DataCenters instance() {
        return instance;
    }

    public static void setInstance(DataCenters dcs) {
        DataCenters.instance = dcs;
    }

    private StringConfig dataCentersConfig;

    private DataCentersConfig data;

    public DataCenters() {
        this.dataCentersConfig = ReloadableConfigFactory.create("[common.topo.datacenters]", StringConfig.class);
        this.data = new DataCentersConfig(dataCentersConfig.get());
        validateCurrentDataCenter(this.data);

        ConfigService.ConfigListener configListener = new ConfigService.ConfigListener() {
            @Override
            public void onConfigChanged(String configKey, String newValue) {
                reload();
            }
        };
        this.dataCentersConfig.setConfigListener(configListener);
    }

    public DataCenter getDataCenter(int dc) {
        return data.getDataCenter(dc);
    }

    public DataCenter getDataCenter(String dcName) {
        return data.getDataCenter(dcName);
    }

    public List<Integer> getDataCenterIds() {
        return data.getDataCenterIds();
    }

    public String getDataCenterUrl(String dataCenterName) {
        return data.getDataCenterUrl(dataCenterName);
    }

    public String getDataCenterUrl(int dataCenterId) {
        return data.getDataCenterUrl(dataCenterId);
    }

    public boolean hasDataCenter(int dc) {
        return data.hasDataCenter(dc);
    }

    public boolean hasDataCenter(String dcName) {
        return data.hasDataCenter(dcName);
    }

    public boolean isLocalDataCenter(int dc) {
        DataCentersConfig data = this.data;
        if (!data.hasDataCenter(dc)) {
            return false;
        }
        return getConfigService().getConfigContext().getDataCenter().equals(data.getDataCenter(dc).getName());
    }

    public boolean isLocalDataCenter(String dcName) {
        DataCentersConfig data = this.data;
        if (!data.hasDataCenter(dcName)) {
            return false;
        }
        return getConfigService().getConfigContext().getDataCenter().equals(dcName);
    }

    public String currentDataCenter() {
        return getConfigService().getConfigContext().getDataCenter();
    }

    public int currentDataCenterId() {
        String dcName = getConfigService().getConfigContext().getDataCenter();
        DataCenter dataCenter = this.data.getDataCenter(dcName);
        if (dataCenter == null) {
            throw new IllegalArgumentException("dataCenter with name: " + dcName + " doesn't exists.");
        }

        return dataCenter.getId();
    }

    private void reload() {
        try {
            DataCentersConfig newData = new DataCentersConfig(dataCentersConfig.get());
            validateCurrentDataCenter(newData);
            this.data = newData;
        } catch (Exception ex) {
            logger.error("Failed to load new datacenters configuration: \n" +
                "dataCenters: " + this.dataCentersConfig.get(), ex);
            // continue to use existing configuration.
        }
    }

    private void validateCurrentDataCenter(DataCentersConfig data) {
        // validate current data center
        try {
            data.getDataCenter(getConfigService().getConfigContext().getDataCenter());
        } catch (Exception ex) {
            throw new RuntimeException("Current datacenter is not configured: " + getConfigService().getConfigContext().getDataCenter());
        }
    }

    private ConfigService getConfigService() {
        return ConfigServiceManager.instance();
    }
}
