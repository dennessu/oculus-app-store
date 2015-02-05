/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.configuration.topo.model;

import com.junbo.common.error.AppCommonErrors;
import com.junbo.configuration.topo.DataCenters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Topology.
 */
public class TopologyConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(TopologyConfig.class);

    private String appUrlTemplate;

    public TopologyConfig(
            String appUrlTemplate) {
        setAppUrlTemplate(appUrlTemplate);
    }

    public String getAppServerUrl(int shard) {
        if (shard >= getNumberOfShards()) {
            LOGGER.warn("No app server for shardId: {}", shard);
            throw AppCommonErrors.INSTANCE.fieldInvalid("id").exception();
        }
        return appUrlTemplate.replace("{shard}", String.valueOf(shard));
    }

    public boolean isHandledBySelf(int shard) {
        return DataCenters.instance().isLocalShard(shard);
    }

    public int getNumberOfShards() {
        DataCenters dataCenters = DataCenters.instance();
        return dataCenters.getDataCenter(dataCenters.currentDataCenterId()).getNumberOfShard();
    }

    public int getRandomShardId() {
        Set<Integer> shardRange = DataCenters.instance().currentShardRange();
        return (int) shardRange.toArray()[ThreadLocalRandom.current().nextInt(shardRange.size())];
    }

    private void setAppUrlTemplate(String appUrlTemplate) {
        this.appUrlTemplate = appUrlTemplate;
    }
}
