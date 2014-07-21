/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.configuration.topo;

import com.junbo.configuration.topo.model.TopologyConfig;
import com.junbo.configuration.ConfigService;
import com.junbo.configuration.reloadable.StringConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

/**
 * Topology.
 */
public class Topology implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(Topology.class);

    private String appHostUrl;
    private StringConfig appUrlTemplateConfig;
    private StringConfig appServersConfig;
    private StringConfig otherServersConfig;

    private TopologyConfig topologyConfig;
    private ConfigService configService;

    public Topology() {}

    @Required
    public void setAppHostUrl(String appHostUrl) {
        this.appHostUrl = appHostUrl;
    }

    @Required
    public void setAppUrlTemplateConfig(StringConfig appUrlTemplateConfig) {
        this.appUrlTemplateConfig = appUrlTemplateConfig;
    }

    @Required
    public void setAppServersConfig(StringConfig appServersConfig) {
        this.appServersConfig = appServersConfig;
    }

    public void setOtherServersConfig(StringConfig otherServersConfig) {
        this.otherServersConfig = otherServersConfig;
    }

    @Required
    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        topologyConfig = new TopologyConfig(
                this.appHostUrl,
                this.appUrlTemplateConfig.get(),
                this.appServersConfig.get(),
                this.otherServersConfig.get(),
                configService);

        ConfigService.ConfigListener configListener = new ConfigService.ConfigListener() {
            @Override
            public void onConfigChanged(String configKey, String newValue) {
                reload();
            }
        };
        this.appUrlTemplateConfig.setConfigListener(configListener);
        this.appServersConfig.setConfigListener(configListener);
    }

    public String getAppServerUrl(int shard) {
        return topologyConfig.getAppServerUrl(shard);
    }

    public boolean isHandledBy(int shard, String ipAddress, int port) {
        return topologyConfig.isHandledBy(shard, ipAddress, port);
    }

    public boolean isHandledBySelf(int shard) {
        return topologyConfig.isHandledBySelf(shard);
    }

    public String getSelfUrl() {
        return topologyConfig.getSelfUrl();
    }

    /**
     * Get the total number of shards int the topology.
     * @return
     */
    public int getNumberOfShards() {
        return topologyConfig.getNumberOfShards();
    }

    /**
     * Generate a random shard id handled by current server.
     * @return the shard id controlled by current server
     */
    public int getRandomShardId() {
        return topologyConfig.getRandomShardId();
    }

    public int getCurrentDCId() {
        return topologyConfig.getDCId();
    }

    /**
     * Shards handled by current server.
     * @return an array of shards handled by current server.
     */
    public int[] handledShards() {
        return topologyConfig.handledShards();
    }

    private void reload() {
        try {
            TopologyConfig newTopologyConfig = new TopologyConfig(
                    this.appHostUrl,
                    this.appUrlTemplateConfig.get(),
                    this.appServersConfig.get(),
                    this.otherServersConfig.get(),
                    configService);
            this.topologyConfig = newTopologyConfig;
        } catch (Exception ex) {
            logger.error("Failed to load new topology configuration: \n" +
                "appUrlTemplate: " + this.appUrlTemplateConfig.get() + "\n" +
                "appServers: " + this.appServersConfig.get(),
                "otherServers: " + this.otherServersConfig.get(), ex);
            // continue to use existing configuration.
        }
    }
}
