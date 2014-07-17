/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.configuration.topo.model;

import com.junbo.configuration.ConfigService;
import com.junbo.configuration.topo.DataCenters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

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

    private String appUrlTemplate;

    // app servers in current dc. first index is shardId and maps to a list of appServers
    private AppServer[][] appServers;

    // map from server ip/port to appserver object
    private Map<String, AppServer> appServerByName;

    // set of other servers, key by ip/port
    private Set<String> otherServers;

    // the app server hosted in this jvm
    private AppServer myAppServer;

    private ConfigService configService;

    public TopologyConfig(
            String appHostUrl,
            String appUrlTemplate,
            String appServersConfig,
            String otherServersConfig,
            ConfigService configService) {
        this.configService = configService;

        setAppUrlTemplate(appUrlTemplate);
        parseAppServers(appServersConfig);
        parseOtherServers(otherServersConfig);

        findMyAppServer(appHostUrl);
    }

    public String getAppServerUrl(int shard) {
        AppServer[] shardAppServers = getAppServers(shard);
        if (shardAppServers.length == 0) {
            throw new RuntimeException("No app server for shardId: " + shard);
        }
        int pick = ThreadLocalRandom.current().nextInt(shardAppServers.length);
        return String.format(appUrlTemplate, shardAppServers[pick].getIpPort());
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

    public String getSelfUrl() {
        return String.format(appUrlTemplate, myAppServer.getIpPort());
    }

    public int getNumberOfShards() {
        return appServers.length;
    }

    public int getRandomShardId() {
        int[] myShards = myAppServer.getShards();
        return myShards[ThreadLocalRandom.current().nextInt(myShards.length)];
    }

    public int getDCId() {
        return myAppServer.getDcId();
    }

    public int[] handledShards() {
        return myAppServer.getShards();
    }

    public boolean isOtherServer(String server) {
        return otherServers.contains(server);
    }

    private AppServer[] getAppServers(int shard) {
        if (!(0 <= shard && shard < appServers.length)) {
            throw new RuntimeException("Invalid shardId: " + shard);
        }
        return appServers[shard];
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

    private static final String APPSERVER_REGEX_IP_COMP = "((25[0-5])|(2[0-4][0-9])|([0-1][0-9][0-9])|([0-9][0-9])|[0-9])";
    private static final String APPSERVER_REGEX_IP = "\\s*(?<ip>" +
            APPSERVER_REGEX_IP_COMP + "\\." +
            APPSERVER_REGEX_IP_COMP + "\\." +
            APPSERVER_REGEX_IP_COMP + "\\." +
            APPSERVER_REGEX_IP_COMP + "):(?<port>\\d+)\\s*;";
    private static final String APPSERVER_REGEX_SHARDS = "\\s*(?<shardsfrom>\\d+)(\\.\\.(?<shardsto>\\d+))?\\s*;";
    private static final String APPSERVER_REGEX_DC = "\\s*(?<dc>[^\\s;,]+)\\s*";
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

            logger.debug("Found appserver: {}:{} {}..{} {}", ip, port, shardsFrom, shardsTo, dc);
            if (!DataCenters.instance().isLocalDataCenter(dc)) {
                logger.debug("Remote dc, ignored.");
                continue;
            }
            DataCenter dataCenter = DataCenters.instance().getDataCenter(dc);
            AppServer appServer = new AppServer(ip, port, shards, dataCenter.getId());
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
                appServer = new AppServer(ip, port, newShards, dataCenter.getId());
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

    private void parseOtherServers(String otherServersConfig) {
        otherServers = new HashSet<>();
        String[] servers = otherServersConfig.split(",");
        for (String server : servers) {
            String trimmedServer = server.trim();
            if (!StringUtils.isEmpty(trimmedServer)) {
                otherServers.add(trimmedServer);
            }
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
            boolean isFound = false;
            for (String ipAddress : ipAddresses) {
                if (otherServers.contains(ipAddress)) {
                    isFound = true;
                    myAppServer = new AppServer(ipAddress, appHostPort, new int[0], DataCenters.instance().currentDataCenterId());
                    break;
                }
            }
            if (!isFound) {
                throw new RuntimeException("App server not found in configuration. IpAddresses: " + Arrays.toString(ipAddresses.toArray()));
            }
        }
    }
}
