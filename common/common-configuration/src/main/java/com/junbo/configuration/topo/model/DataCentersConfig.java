/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.configuration.topo.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DataCenters.
 */
public class DataCentersConfig {
    private static final Logger logger = LoggerFactory.getLogger(DataCentersConfig.class);

    // datacenter id to datacenter object
    private DataCenter[] datacenters;

    // map from datacenter name to datacenter object
    private Map<String, DataCenter> datacenterByName;

    public DataCentersConfig(String dataCentersConfig) {
        parseDataCenters(dataCentersConfig);
    }

    public String getDataCenterUrl(String dataCenterName) {
        DataCenter dataCenter = datacenterByName.get(dataCenterName);
        return dataCenter.getUrl();
    }

    public String getDataCenterUrl(int dataCenterId) {
        DataCenter dataCenter = getDataCenter(dataCenterId);
        return dataCenter.getUrl();
    }

    public boolean hasDataCenter(int dc) {
        return 0 <= dc && dc < datacenters.length;
    }

    public boolean hasDataCenter(String dcName) {
        return datacenterByName.containsKey(dcName);
    }

    public DataCenter getDataCenter(int dc) {
        if (!hasDataCenter(dc)) {
            throw new RuntimeException("Invalid datacenter id: " + dc);
        }
        return datacenters[dc];
    }

    public DataCenter getDataCenter(String dcName) {
        if (!hasDataCenter(dcName)) {
            throw new RuntimeException("Invalid datacenter name: " + dcName);
        }
        return datacenterByName.get(dcName);
    }

    public List<Integer> getDataCenterIds() {
        if (datacenters == null) {
            throw new RuntimeException("Datacenter doesn't exists.");
        }
        List<Integer> ids = new ArrayList<>();
        for (int index = 0; index < datacenters.length; index ++) {
            ids.add(datacenters[index].getId());
        }
        return ids;
    }
    private static final String DATACENTER_REGEX_SINGLE_ENTRY =
            "\\s*(?<url>[^\\s;,]+)\\s*;\\s*(?<id>\\d+)\\s*;\\s*(?<name>[^\\s;,]+)\\s*;(?<numberOfShard>\\d+)(,|$|(,\\\\s*$))";
    private static final String DATACENTER_REGEX_FULL = "^(" + DATACENTER_REGEX_SINGLE_ENTRY + ")+$";
    private static final Pattern DATACENTER_PATTERN_SINGLE_ENTRY = Pattern.compile(DATACENTER_REGEX_SINGLE_ENTRY);
    private static final Pattern DATACENTER_PATTERN_FULL = Pattern.compile(DATACENTER_REGEX_FULL);
    private void parseDataCenters(String dataCentersConfig) {
        if (!DATACENTER_PATTERN_FULL.matcher(dataCentersConfig).matches()) {
            throw new RuntimeException("Invalid datacenter configuration: " + dataCentersConfig);
        }
        datacenterByName = new HashMap<>();

        int totalCount = 0;
        Matcher matcher = DATACENTER_PATTERN_SINGLE_ENTRY.matcher(dataCentersConfig);
        while (matcher.find()) {
            String url = matcher.group("url");
            int id = Integer.parseInt(matcher.group("id"));
            String name = matcher.group("name");
            int numberOfShard = Integer.parseInt(matcher.group("numberOfShard"));

            logger.debug("Found datacenter: {} {} {} {}", url, id, name, numberOfShard);

            DataCenter dc = new DataCenter(url, id, name, numberOfShard);
            if (datacenterByName.containsKey(dc.getName())) {
                throw new RuntimeException("Duplicated datacenter name: " + dc.getName());
            }
            datacenterByName.put(dc.getName(), dc);
            ++totalCount;
        }

        datacenters = new DataCenter[totalCount];
        for (DataCenter dc : datacenterByName.values()) {
            if (!(0 <= dc.getId() && dc.getId() < totalCount)) {
                throw new RuntimeException("DataCenter id out of range: " + dc.getId() + " total dc count: " + totalCount);
            }
            if (datacenters[dc.getId()] != null) {
                throw new RuntimeException("Duplicated datacenter id: " + dc.getId());
            }

            datacenters[dc.getId()] = dc;
        }

        // not really necessary because there is no duplicates and all ids are in range
        // so this should always be true. Just being defensive.
        for (int i = 0; i < totalCount; ++i) {
            if (datacenters[i] == null) {
                throw new RuntimeException("Missing datacenter with id: " + i);
            }
        }
    }
}
