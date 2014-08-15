/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 SilkCloud and/or its affiliates. All rights reserved.
 */
package com.junbo.common.job.cache;

import com.junbo.common.cloudant.client.CloudantUri;
import com.junbo.common.memcached.JunboMemcachedClient;
import net.spy.memcached.MemcachedClientIF;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

/**
 * CacheSnifferJob.
 */
public class CacheSnifferJob implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheSnifferJob.class);

    private static MemcachedClientIF memcachedClient = JunboMemcachedClient.instance();

    private static final String FEED_SEPARATOR = "\r\n";
    private static final String MEMCACHED_EXPIRATION_KEY = "common.memcached.expiration";
    private static final String SEQ_KEY = "seq";
    private static final String ID_KEY = "id";

    private Integer expiration;

    @Override
    public void afterPropertiesSet() throws Exception {
        // initialize configuration
        this.expiration = SnifferUtils.safeParseInt(SnifferUtils.getConfig(MEMCACHED_EXPIRATION_KEY));
    }

    public void listen() {
        LOGGER.info("Start listening cloundant DB change feed");

        List<CloudantUri> cloudantInstances = CloudantSniffer.instance().getCloudantInstances();

        for (CloudantUri cloudantUri : cloudantInstances) {
            List<String> databases = CloudantSniffer.instance().getAllDatabases(cloudantUri);

            for (String database : databases) {
                LOGGER.debug("Try to fetch last change token from memcached.");
                String lastChange = getCache(buildLastChangeKey(cloudantUri, database));

                if (StringUtils.isEmpty(lastChange)) {
                    LOGGER.debug("Last change token is missing in memcached, fetch from cloudant instead.");
                    lastChange = CloudantSniffer.instance().getLastChange(cloudantUri, database);
                }

                // listen database continuous feed
                listenDatabaseChanges(cloudantUri, database, lastChange);
            }
        }
    }

    private void listenDatabaseChanges(final CloudantUri cloudantUri, final String database, final String lastChange) {
        String threadName = "cloudant-listener-" + database;

        new Thread(new Runnable() {
            public void run() {
                InputStream feed = CloudantSniffer.instance().getChangeFeed(cloudantUri, database, lastChange);

                BufferedReader reader = new BufferedReader(new InputStreamReader(feed));
                String change;
                while (true) {
                    try {
                        change = reader.readLine();

                        if (change != null) {
                            handleChange(cloudantUri, database, change);
                        }
                    } catch (Exception e) {
                        LOGGER.error("Error occurred during receiving cloundant change feed.", e);
                    }
                }
            }
        }, threadName).start();
    }

    private void handleChange(CloudantUri cloudantUri, String database, String change) {
        LOGGER.debug("Receive change feed " + change);

        if (FEED_SEPARATOR.equals(change) || StringUtils.isEmpty(change)) {
            LOGGER.debug("Invalid change feed payload.");
            return;
        }

        Map<String, Object> payload = SnifferUtils.parse(change, Map.class);

        // update last seq in memcached
        Object seqObj = payload.get(SEQ_KEY);
        String seq = seqObj == null ? null : seqObj.toString();

        updateCache(buildLastChangeKey(cloudantUri, database), seq);

        // evict entity cache
        Object entityId = payload.get(ID_KEY);

        if (entityId != null) {
            String entityKey = entityId.toString() + ":" + database + ":" + cloudantUri.getDc();
            deleteCache(entityKey);
        }

        return;
    }

    private void updateCache(String key, String value) {
        if (!checkCache(key)) {
            return;
        }

        try {
            memcachedClient.set(key, this.expiration, value).get();
            LOGGER.debug("Memcached updated successfully. [key]" + key + " [value]" + value);
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
            LOGGER.debug("Memcached deleted successfully. [key]" + key);
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
