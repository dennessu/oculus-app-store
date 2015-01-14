/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.configuration.topo;

import com.junbo.configuration.ConfigService;
import com.junbo.configuration.ConfigServiceManager;
import com.junbo.configuration.reloadable.StringConfig;
import com.junbo.configuration.topo.model.TopologyConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

/**
 * Topology.
 */
public class Topology implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(Topology.class);
    private static final String SELF_URL = ConfigServiceManager.instance().getConfigValue("apphost.server.uri")
            .replace("0\\.0\\.0\\.0", ConfigServiceManager.instance().getConfigContext().getIpAddresses().get(0));

    private StringConfig appUrlTemplateConfig;

    private TopologyConfig topologyConfig;

    public Topology() {
    }

    @Required
    public void setAppUrlTemplateConfig(StringConfig appUrlTemplateConfig) {
        this.appUrlTemplateConfig = appUrlTemplateConfig;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        topologyConfig = new TopologyConfig(this.appUrlTemplateConfig.get());

        ConfigService.ConfigListener configListener = new ConfigService.ConfigListener() {
            @Override
            public void onConfigChanged(String configKey, String newValue) {
                reload();
            }
        };
        this.appUrlTemplateConfig.setConfigListener(configListener);
    }

    public String getAppServerUrl(int shard) {
        return topologyConfig.getAppServerUrl(shard);
    }

    public boolean isHandledBySelf(int shard) {
        return topologyConfig.isHandledBySelf(shard);
    }

    public String getSelfUrl() {
        return SELF_URL;
    }

    /**
     * Get the total number of shards int the topology.
     *
     * @return
     */
    public int getNumberOfShards() {
        return topologyConfig.getNumberOfShards();
    }

    public int getRandomShardId() {
        return topologyConfig.getRandomShardId();
    }

    public int getCurrentDCId() {
        return DataCenters.instance().currentDataCenterId();
    }

    private void reload() {
        try {
            TopologyConfig newTopologyConfig = new TopologyConfig(this.appUrlTemplateConfig.get());
            this.topologyConfig = newTopologyConfig;
        } catch (Exception ex) {
            logger.error("Failed to load new topology configuration: \n" +
                    "appUrlTemplate: " + this.appUrlTemplateConfig.get() + "\n");
            // continue to use existing configuration.
        }
    }
}
