/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 SilkCloud and/or its affiliates. All rights reserved.
 */
package com.junbo.common.job.cache;

import com.junbo.common.cloudant.client.CloudantUri;
import com.junbo.common.memcached.JunboMemcachedClient;
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

    private static final String FEED_SEPARATOR = "\r\n";
    private static final String SEQ_KEY = "seq";
    private static final String ID_KEY = "id";
    private static final String CHANGES_KEY = "changes";
    private static final String CLOUDANT_HEARTBEAT_KEY = "common.cloudant.heartbeat";
    private static final String SNIFFER_ENABLED_KEY = "common.jobs.sniffer.enabled";

    private static final int SAFE_SLEEP = 5000;
    private static final int MONITOR_THREADS = 10;

    private boolean enabled;
    private Integer expiration;
    private Integer connectionTimeout;

    private ScheduledExecutorService executor;

    @Override
    public void afterPropertiesSet() throws Exception {
        // initialize configuration
        this.enabled = SnifferUtils.safeParseBoolean(SnifferUtils.getConfig(SNIFFER_ENABLED_KEY));
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
        String lastChange = getCache(buildLastChangeKey(cloudantUri, database));

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
                                LOGGER.error("Invalid cloudant chagne feed received.");
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
        LOGGER.trace("Receive change feed " + change);

        if (FEED_SEPARATOR.equals(change) || StringUtils.isEmpty(change)) {
            LOGGER.debug("Cloundant heartbeat payload received.");
            return;
        }

        Map<String, Object> payload = SnifferUtils.parse(change, Map.class);

        // update last seq in memcached
        Object seqObj = payload.get(SEQ_KEY);
        String seq = seqObj == null ? null : seqObj.toString();

        updateCache(buildLastChangeKey(cloudantUri, database), seq);

        // evict entity cache
        Object entityId = payload.get(ID_KEY);
        List changes = (List)payload.get(CHANGES_KEY);

        if (entityId != null) {
            String entityIdStr = entityId.toString();
            if (entityIdStr.startsWith("_")) {
                // system data updated, ignore
                LOGGER.debug("Received entity change key {}, ignored", entityIdStr);
                return;
            }

            boolean cacheIsValid = false;
            String entityKey = entityIdStr + ":" + database + ":" + cloudantUri.getDc();

            boolean revFound = false;
            try {
                // best effort: don't clear cache if the new rev is already in cache.
                if (changes != null && changes.size() == 1) {
                    Map changedFields = (Map) changes.get(0);
                    if (changedFields != null && changedFields.containsKey("rev")) {
                        Object revObject = changedFields.get("rev");
                        String rev = (revObject == null ? null : revObject.toString());

                        if (!StringUtils.isEmpty(rev)) {
                            String existingValue = getCache(entityKey);
                            if (existingValue == null) {
                                // not in cache, so cache is valid
                                cacheIsValid = true;
                                revFound = true;
                                LOGGER.debug("Received entity change {} rev {} but not found in cache", entityKey, rev);
                            } else {
                                Map<String, Object> entity = SnifferUtils.parse(existingValue, Map.class);

                                if (entity != null && entity.containsKey("_rev")) {
                                    revFound = true;
                                    if (rev.compareTo((String)entity.get("_rev")) <= 0) {
                                        cacheIsValid = true;
                                        LOGGER.debug("Valid cached rev {} for {} change rev {}", entity.get("_rev"), entityKey, rev);
                                    } else {
                                        LOGGER.debug("Invalid cached rev {} for {} change rev {}", entity.get("_rev"), entityKey, rev);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                LOGGER.error("Failed to compare cache rev with change for key {}. Default to delete existing cache.", entityKey, ex);
                cacheIsValid = false;
            } finally {
                // add a trace anyway
                if (!revFound) {
                    LOGGER.warn("Received entity change key {}, rev not found. full change: {}", entityKey, change);
                }
            }

            if (!cacheIsValid) {
                deleteCache(entityKey);
            }
        }

        return;
    }

    private void updateCache(String key, String value) {
        if (!checkCache(key)) {
            return;
        }
        if (StringUtils.isEmpty(value)) {
            return;
        }

        try {
            memcachedClient.set(key, this.expiration, value).get();
            LOGGER.trace("Memcached updated successfully. [key]" + key + " [value]" + value);
        } catch (Exception e) {
            LOGGER.warn("Error writing to memcached.", e);
        }
    }

    private String getCache(String key) {
        if (!checkCache(key)) {
            return null;
        }

        return (String) memcachedClient.get(key);
    }

    void deleteCache(String key) {
        checkCache(key);

        try {
            memcachedClient.delete(key).get();
            LOGGER.trace("Memcached deleted successfully. [key]" + key);
        } catch (Exception e) {
            LOGGER.warn("Error deleting from memcached.", e);
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
}
