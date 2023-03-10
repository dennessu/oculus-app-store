/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 SilkCloud and/or its affiliates. All rights reserved.
 */
package com.junbo.common.sniffer;

import com.junbo.common.cloudant.client.CloudantUri;
import com.junbo.common.memcached.JunboMemcachedClient;
import com.junbo.configuration.ConfigService;
import com.junbo.configuration.ConfigServiceManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * CacheSnifferJob.
 */
public class CacheSnifferJob implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheSnifferJob.class);

    private static JunboMemcachedClient memcachedClient = JunboMemcachedClient.instance();
    private static JunboMemcachedClient memcachedClient2 = createMemcacheClient2();

    private static final String FEED_SEPARATOR = "\r\n";
    private static final String SEQ_KEY = "seq";
    private static final String ID_KEY = "id";
    private static final String CHANGES_KEY = "changes";
    private static final String CLOUDANT_HEARTBEAT_KEY = "common.cloudant.heartbeat";
    private static final String SNIFFER_ENABLED_KEY = "common.jobs.sniffer.enabled";
    private static final String DBNAME_PREFIX = "common.cloudant.dbNamePrefix";

    private static final int SAFE_SLEEP = 5000;
    private static final int MONITOR_THREADS = 10;

    private boolean enabled;
    private String dbNamePrefix;
    private Integer expiration;
    private Integer connectionTimeout;

    private ScheduledExecutorService executor;

    @Override
    public void afterPropertiesSet() throws Exception {
        // initialize configuration
        this.enabled = SnifferUtils.safeParseBoolean(SnifferUtils.getConfig(SNIFFER_ENABLED_KEY));
        this.dbNamePrefix = SnifferUtils.getConfig(DBNAME_PREFIX);
        this.expiration = 0; //forever

        // initialize cloudant feed connection timeout
        Integer cloudantHeartbeat = SnifferUtils.safeParseInt(SnifferUtils.getConfig(CLOUDANT_HEARTBEAT_KEY));
        this.connectionTimeout = cloudantHeartbeat + cloudantHeartbeat / 10;
    }

    public void listen() {
        if (!this.enabled) {
            LOGGER.info("Sniffer is OFF in current configuration.");
            return;
        }

        LOGGER.info("Start listening cloundant DB change feed");

        List<CloudantUri> cloudantInstances = CloudantSniffer.instance().getCloudantInstances();
        List<String> allDatabases = CloudantSniffer.instance().getDatabaseList();

        int totalDatabases = 0;

        // collect all databases
        Map<String, List<String>> databaseMap = new HashMap<>();
        for (CloudantUri cloudantUri : cloudantInstances) {
            List<String> databases = CloudantSniffer.instance().getAllDatabases(cloudantUri, allDatabases);
            totalDatabases += databases.size();

            databaseMap.put(cloudantUri.getKey(), databases);
        }

        LOGGER.info("Found {} databases in cloudant.", totalDatabases);
        if (totalDatabases != allDatabases.size()) {
            LOGGER.error("Found {} databases in cloudant and expected {} databases in db list file. ", totalDatabases, allDatabases.size());
            throw new RuntimeException(String.format("Found %d databases in cloudant and expected %d databases in db list file. ", totalDatabases, allDatabases.size()));
        }

        executor = Executors.newScheduledThreadPool(totalDatabases + MONITOR_THREADS);

        for (CloudantUri cloudantUri : cloudantInstances) {
            List<String> databases = databaseMap.get(cloudantUri.getKey());

            for (String database : databases) {
                // listen database continuous feed
                LOGGER.info("Start listening database [" + database + "] on instance [" + cloudantUri.getDetail() + "]");

                listenDatabaseChanges(cloudantUri, database);
            }
        }
    }

    private String getLastChange(CloudantUri cloudantUri, String database) {
        LOGGER.trace("Try to fetch last change token from memcached.");
        String lastChange = getCache(memcachedClient, buildLastChangeKey(cloudantUri, database));

        if (StringUtils.isEmpty(lastChange)) {
            LOGGER.trace("Last change token is missing in memcached, fetch from cloudant instead.");
            lastChange = CloudantSniffer.instance().getLastChange(cloudantUri, database);
        }

        return lastChange;
    }

    private void listenDatabaseChanges(final CloudantUri cloudantUri, final String database) {
        String threadName = "cloudant-listener-" + database;

        new Thread(new Runnable() {
            public void run() {
                outer:
                for (; ; SnifferUtils.sleep(SAFE_SLEEP)) {
                    try {
                        String lastChange = getLastChange(cloudantUri, database);
                        updateCache(buildLastChangeKey(cloudantUri, database), lastChange);

                        InputStream feed = CloudantSniffer.instance().getChangeFeed(cloudantUri, database, lastChange);
                        final BufferedReader reader = new BufferedReader(new InputStreamReader(feed));

                        String change;
                        while (true) {
                            change = executeWithTimeout(
                                    new Callable<String>() {
                                        public String call() throws Exception {
                                            // blocking read
                                            return reader.readLine();
                                        }
                                    }, connectionTimeout);

                            if (change == null) {
                                //theoretically, the code will not be reached
                                LOGGER.error("Invalid cloudant change feed received.");
                                continue outer;
                            }

                            handleChange(cloudantUri, database, change);
                        }
                    } catch (Exception e) {
                        LOGGER.error("Error occurred during receiving cloundant change feed.", e);
                        continue outer;
                    }
                }
            }
        }, threadName).start();
    }

    private <T> T executeWithTimeout(Callable<T> callable, Integer timeout)
            throws ExecutionException, InterruptedException {
        final Future<T> handler = executor.submit(callable);

        ScheduledFuture monitor = executor.schedule(new Runnable() {
            @Override
            public void run() {
                if (!handler.isDone()) {
                    LOGGER.warn("Cloudant change feed connection is broken.");
                    handler.cancel(true);
                }
            }
        }, timeout, TimeUnit.MILLISECONDS);

        // blocking wait
        T result = handler.get();
        try {
            LOGGER.trace("Cancel feed change monitor thread.");
            monitor.cancel(true);
        } catch (Exception e) {
            //silently ignore
        }

        return result;
    }

    private void handleChange(CloudantUri cloudantUri, String database, String change) {
        LOGGER.trace("[received raw] " + change);

        if (FEED_SEPARATOR.equals(change) || StringUtils.isEmpty(change)) {
            LOGGER.debug("[heartbeat]");
            return;
        }

        Map<String, Object> payload = SnifferUtils.parse(change, Map.class);

        // update last seq in memcached
        Object seqObj = payload.get(SEQ_KEY);
        String seq = seqObj == null ? null : seqObj.toString();

        if (seq == null) {
            LOGGER.warn("Unexpected change row: {}", change);
            return;
        }

        updateCache(buildLastChangeKey(cloudantUri, database), seq);

        // evict entity cache
        Object entityId = payload.get(ID_KEY);
        List changes = (List)payload.get(CHANGES_KEY);

        if (entityId != null) {
            String entityIdStr = entityId.toString();
            if (entityIdStr.startsWith("_")) {
                // system data updated, ignore
                LOGGER.debug("[ignored] {}", entityIdStr);
                return;
            }

            String databaseWithoutPrefix = SnifferUtils.removePrefix(database, dbNamePrefix);
            String rawCacheEntityKey = entityIdStr + ":" + databaseWithoutPrefix;
            invalidateCache(change, changes, rawCacheEntityKey, "_rev");

            String cookedCacheEntityKey = "<COOKED>" + SnifferUtils.encodeId(entityIdStr, databaseWithoutPrefix);
            invalidateCache(change, changes, cookedCacheEntityKey, "rev");
        }

        return;
    }

    private void invalidateCache(String change, List changes, String entityKey, String entityRevKey) {
        invalidateCache(memcachedClient, change, changes, entityKey, entityRevKey);
        if (memcachedClient2 != null) {
            invalidateCache(memcachedClient2, change, changes, entityKey, entityRevKey);
        }
    }

    private void invalidateCache(JunboMemcachedClient memcachedClient, String change, List changes, String entityKey, String entityRevKey) {
        boolean cacheIsValid = false;

        boolean revFound = false;
        try {
            // best effort: don't clear cache if the new rev is already in cache.
            if (changes != null && changes.size() == 1) {
                Map changedFields = (Map) changes.get(0);
                if (changedFields != null && changedFields.containsKey("rev")) {
                    Object revObject = changedFields.get("rev");
                    String rev = (revObject == null ? null : revObject.toString());

                    if (!StringUtils.isEmpty(rev)) {
                        String existingValue = getCache(memcachedClient, entityKey);
                        if (existingValue == null) {
                            // not in cache, so cache is valid
                            cacheIsValid = true;
                            revFound = true;
                            LOGGER.debug("[miss] {}:{}@{}", memcachedClient.getId(), entityKey, rev);
                        } else {
                            Map<String, Object> entity = SnifferUtils.parse(existingValue, Map.class);

                            if (entity != null && entity.containsKey(entityRevKey)) {
                                revFound = true;
                                String entityRev = (String)entity.get(entityRevKey);
                                if (rev.equals(entityRev)) {
                                    cacheIsValid = true;
                                    LOGGER.debug("[valid] {}:{}@{}", memcachedClient.getId(), entityKey, entityRev);
                                } else {
                                    LOGGER.debug("[invalid] {}:{}@{} r@{}", memcachedClient.getId(), entityKey, entityRev, rev);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.error("Failed to compare cache rev with change for key {}:{}. Default to delete existing cache.", memcachedClient.getId(), entityKey, ex);
            cacheIsValid = false;
        } finally {
            // add a trace anyway
            if (!revFound) {
                LOGGER.warn("Received entity change key {}:{}, rev not found. full change: {}", memcachedClient.getId(), entityKey, change);
            }
        }

        if (!cacheIsValid) {
            deleteCache(memcachedClient, entityKey);
        }
    }

    private void updateCache(String key, String value) {
        updateCache(memcachedClient, key, value);
        if (memcachedClient2 != null) {
            updateCache(memcachedClient2, key, value);
        }
    }

    private void updateCache(JunboMemcachedClient memcachedClient, String key, String value) {
        if (!checkCache(key)) {
            return;
        }
        if (StringUtils.isEmpty(value)) {
            return;
        }

        try {
            memcachedClient.set(key, this.expiration, value).get();
            LOGGER.trace("[updated cache] " + memcachedClient.getId() + " [key]" + key + " [value]" + value);
        } catch (Exception e) {
            LOGGER.warn("Error writing to memcached " + memcachedClient.getId(), e);
        }
    }

    private String getCache(JunboMemcachedClient memcachedClient, String key) {
        if (!checkCache(key)) {
            return null;
        }

        return (String) memcachedClient.get(key);
    }

    void deleteCache(JunboMemcachedClient memcachedClient, String key) {
        checkCache(key);

        try {
            memcachedClient.delete(key).get();
            LOGGER.trace("[deleted cache] " + memcachedClient.getId() + " [key]" + key);
        } catch (Exception e) {
            LOGGER.warn("Error deleting from memcached " + memcachedClient.getId(), e);
        }
    }

    private boolean checkCache(String key) {
        if (memcachedClient == null) {
            LOGGER.warn("Memcached client should not be null.");
            return false;
        }

        if (StringUtils.isEmpty(key)) {
            LOGGER.warn("Memcached key should not be null or trimmed empty.");
            return false;
        }

        return true;
    }

    private String buildLastChangeKey(CloudantUri uri, String database) {
        return String.format("%s/%s/%s/lastseq",
                uri.getValue(),
                SnifferUtils.isNull(uri.getUsername(), ""),
                database);
    }

    /**
     * Create second memcached client because PROD green and PROD blue share the sniffer server,
     * but uses separate memcached servers.
     */
    private static JunboMemcachedClient createMemcacheClient2() {
        ConfigService configService = ConfigServiceManager.instance();
        String memcachedSnifferServers = configService.getConfigValue("common.memcached.sniffer.servers");
        if (StringUtils.isEmpty(memcachedSnifferServers)) {
            return null;
        }

        JunboMemcachedClient client2 = new JunboMemcachedClient();
        client2.setServers(memcachedSnifferServers);

        // copy other properties from the original memcachedClient.
        client2.setId("2");
        client2.setEnabled(memcachedClient.getEnabled());
        client2.setTimeout(memcachedClient.getTimeout());
        client2.setCompressionThreshold(memcachedClient.getCompressionThreshold());
        client2.setUsername(memcachedClient.getUsername());
        client2.setPassword(memcachedClient.getPassword());
        client2.setAuthType(memcachedClient.getAuthType());

        client2.afterPropertiesSet();
        return client2;
    }
}
