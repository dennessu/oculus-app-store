/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.topo.model;

import com.junbo.configuration.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Topology.
 */
public class TopologyConfig {
    private static final Logger logger = LoggerFactory.getLogger(TopologyConfig.class);

    // datacenter id to datacenter object
    private DataCenter[] datacenters;

    // map from datacenter name to datacenter object
    private Map<String, DataCenter> datacenterByName;

    private String appUrlTemplate;

    // app servers in current dc. first index is shardId and maps to a list of appServers
    private AppServer[][] appServers;

    // map from server ip/port to appserver object
    private Map<String, AppServer> appServerByName;

    // the app server hosted in this jvm
    private AppServer myAppServer;

    private ConfigService configService;

    public TopologyConfig(
            String appHostUrl,
            String dataCentersConfig,
            String appUrlTemplate,
            String appServersConfig,
            ConfigService configService) {
        this.configService = configService;

        setAppUrlTemplate(appUrlTemplate);
        parseDataCenters(dataCentersConfig);
        parseAppServers(appServersConfig);

        findMyAppServer(appHostUrl);
    }

    public String getDataCenterUrlByName(String dataCenterName) {
        DataCenter dataCenter = datacenterByName.get(dataCenterName);
        return dataCenter.getUrl();
    }

    public String getDataCenterUrl(int dataCenterId) {
        DataCenter dataCenter = getDataCenter(dataCenterId);
        return dataCenter.getUrl();
    }

    public String getAppServerUrl(int shard) {
        AppServer[] shardAppServers = getAppServers(shard);
        if (shardAppServers.length == 0) {
            throw new RuntimeException("No app server for shardId: " + shard);
        }
        int pick = ThreadLocalRandom.current().nextInt(shardAppServers.length);
        return String.format(appUrlTemplate, shardAppServers[pick].getIpPort());
    }

    public boolean isLocalDatacenter(int dc) {
        return configService.getConfigContext().getDatacenter() == dc;
    }

    public boolean isLocalDatacenter(String dcName) {
        return configService.getConfigContext().getDatacenter() == getDataCenter(dcName).getId();
    }

    public boolean isHandledBy(int shard, String ipAddress, int port) {
        AppServer[] shardAppServers = getAppServers(shard);
        for (AppServer appServer : shardAppServers) {
            if (appServer.getIpAddress().equals(ipAddress) && appServer.getPort() == port) {
                return true;
            }
        }

        return false;
    }

    public boolean isHandledBySelf(int shard) {
        return isHandledBy(shard, myAppServer.getIpAddress(), myAppServer.getPort());
    }

    public int getRandomShardId() {
        int[] myShards = myAppServer.getShards();
        return myShards[ThreadLocalRandom.current().nextInt(myShards.length)];
    }

    public int[] handledShards() {
        return myAppServer.getShards();
    }

    private AppServer[] getAppServers(int shard) {
        if (!(0 <= shard && shard <= appServers.length)) {
            throw new RuntimeException("Invalid shardId: " + shard);
        }
        return appServers[shard];
    }

    private DataCenter getDataCenter(int dc) {
        if (!(0 <= dc && dc <= datacenters.length)) {
            throw new RuntimeException("Invalid datacenter id: " + dc);
        }
        return datacenters[dc];
    }

    private DataCenter getDataCenter(String dcName) {
        if (!datacenterByName.containsKey(dcName)) {
            throw new RuntimeException("Invalid datacenter name: " + dcName);
        }
        return datacenterByName.get(dcName);
    }

    private void setAppUrlTemplate(String appUrlTemplate) {
        try {
            String sampleIpPort = "0.0.0.0:80";
            String formatValues = String.format(appUrlTemplate, sampleIpPort);
            if (!formatValues.contains(sampleIpPort)) {
                throw new RuntimeException("Invalid appUrlTemplate: " + appUrlTemplate);
            }
            this.appUrlTemplate = appUrlTemplate;
        } catch (Exception ex) {
            throw new RuntimeException("Invalid appUrlTemplate: " + appUrlTemplate);
        }
    }

    private static final String DATACENTER_REGEX_SINGLE_ENTRY = "\\s*(?<url>[^;,]+)\\s*;\\s*(?<id>\\d+)\\s*;\\s*(?<name>[^;,]+)\\s*(,|$|(,\\s*$))";
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

            logger.debug("Found datacenter: {} {} {}", url, id, name);

            DataCenter dc = new DataCenter(url, id, name);
            if (datacenterByName.containsKey(dc.getName())) {
                throw new RuntimeException("Duplicated datacenter name: " + dc.getName());
            }
            datacenterByName.put(dc.getName(), dc);
            ++totalCount;
        }

        datacenters = new DataCenter[totalCount];
        for (DataCenter dc : datacenterByName.values()) {
            if (!(0 <= dc.getId() && dc.getId() < totalCount)) {
                throw new RuntimeException("Datacenter id out of range: " + dc.getId() + " total dc count: " + totalCount);
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

    private static final String APPSERVER_REGEX_IP_COMP = "((25[0-5])|(2[0-4][0-9])|([0-1][0-9][0-9])|([0-9][0-9])|[0-9])";
    private static final String APPSERVER_REGEX_IP = "\\s*(?<ip>" +
            APPSERVER_REGEX_IP_COMP + "\\." +
            APPSERVER_REGEX_IP_COMP + "\\." +
            APPSERVER_REGEX_IP_COMP + "\\." +
            APPSERVER_REGEX_IP_COMP + "):(?<port>\\d+)\\s*;";
    private static final String APPSERVER_REGEX_SHARDS = "\\s*(?<shardsfrom>\\d+)(\\.\\.(?<shardsto>\\d+))?\\s*;";
    private static final String APPSERVER_REGEX_DC = "\\s*(?<dc>[^;,]+)\\s*";
    private static final String APPSERVER_REGEX_SINGLE_ENTRY = APPSERVER_REGEX_IP + APPSERVER_REGEX_SHARDS + APPSERVER_REGEX_DC + "(,|$|(,\\s*$))";
    private static final String APPSERVER_REGEX_FULL = "^(" + APPSERVER_REGEX_SINGLE_ENTRY + ")+$";

    private static final Pattern APPSERVER_PATTERN_SINGLE_ENTRY = Pattern.compile(APPSERVER_REGEX_SINGLE_ENTRY);
    private static final Pattern APPSERVER_PATTERN_FULL = Pattern.compile(APPSERVER_REGEX_FULL);
    private void parseAppServers(String appServersConfig) {
        if (!APPSERVER_PATTERN_FULL.matcher(appServersConfig).matches()) {
            throw new RuntimeException("Invalid appservers configuration: " + appServersConfig);
        }
        appServerByName = new HashMap<>();

        int maxShard = 0;
        Matcher matcher = APPSERVER_PATTERN_SINGLE_ENTRY.matcher(appServersConfig);
        while (matcher.find()) {
            String ip = matcher.group("ip");

            int port = Integer.parseInt(matcher.group("port"));
            if (!(0 <= port && port <= 65535)) {
                throw new RuntimeException("Invalid port: " + port + " appServer: " + matcher.group());
            }

            int shardsFrom = Integer.parseInt(matcher.group("shardsfrom"));
            int shardsTo = shardsFrom;
            if (matcher.group("shardsto") != null) {
                shardsTo = Integer.parseInt(matcher.group("shardsto"));
                if (shardsTo < shardsFrom) {
                    throw new RuntimeException("Invalid shard range: " + shardsFrom + ".." + shardsTo + " appServer: " + matcher.group());
                }
            }
            if (shardsTo > maxShard) {
                maxShard = shardsTo;
            }

            int[] shards = new int[shardsTo - shardsFrom + 1];
            for (int i = shardsFrom; i <= shardsTo; ++i) {
                shards[i - shardsFrom] = i;
            }

            String dc = matcher.group("dc");
            if (datacenterByName.get(dc) == null) {
                throw new RuntimeException("Invalid datacenter name: " + dc + " appServer: " + matcher.group());
            }
            int dcId = datacenterByName.get(dc).getId();

            logger.debug("Found appserver: {}:{} {}..{} {} {}", ip, port, shardsFrom, shardsTo, dc, dcId);
            if (dcId != configService.getConfigContext().getDatacenter()) {
                logger.debug("Remote dc, ignored.");
                continue;
            }

            AppServer appServer = new AppServer(ip, port, shards);
            if (appServerByName.containsKey(appServer.getIpPort())) {
                // merge shards
                int[] oldShards = appServerByName.get(appServer.getIpPort()).getShards();
                int[] newShards = new int[oldShards.length + shards.length];

                for (int i = 0; i < oldShards.length; ++i) {
                    newShards[i] = oldShards[i];
                }
                for (int i = 0; i < shards.length; ++i) {
                    newShards[oldShards.length + i] = shards[i];
                }

                Arrays.sort(newShards);
                appServer = new AppServer(ip, port, newShards);
            }
            appServerByName.put(appServer.getIpPort(), appServer);
        }
        int shardCount = maxShard + 1;

        List<AppServer>[] appServerList = new List[shardCount];
        for (AppServer appServer : appServerByName.values()) {
            int[] shards = appServer.getShards();
            for (int shard : shards) {
                if (!(0 <= shard && shard <= maxShard)) {
                    throw new RuntimeException("Shard id out of range: " + shard);
                }
                if (appServerList[shard] == null) {
                    appServerList[shard] = new ArrayList<>();
                }
                appServerList[shard].add(appServer);
            }
        }

        appServers = new AppServer[shardCount][];
        for (int i = 0; i < shardCount; ++i) {
            if (appServerList[i] == null) {
                throw new RuntimeException("Missing configuration for shard id: " + i);
            }
            appServers[i] = appServerList[i].toArray(new AppServer[0]);
        }
    }

    private void findMyAppServer(String appHostUrl) {
        // parse appHostPort
        int appHostPort;
        try {
            appHostPort = new URI(appHostUrl).getPort();
        } catch (URISyntaxException ex) {
            throw new RuntimeException("Invalid appHostUrl: " + appHostUrl);
        }

        // find myAppServer
        List<String> ipAddresses = configService.getConfigContext().getIpAddresses();
        for (String ipAddress : ipAddresses) {
            String ipPort = ipAddress + ":" + appHostPort;
            AppServer newAppServer = appServerByName.get(ipPort);
            if (myAppServer != null) {
                throw new RuntimeException("Found two matching app servers in configuration: " + ipPort + " and " + myAppServer);
            }
            myAppServer = newAppServer;
        }
        if (myAppServer == null) {
            throw new RuntimeException("App server not found in configuration. IpAddresses: " + Arrays.toString(ipAddresses.toArray()));
        }
    }
}
