/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 SilkCloud and/or its affiliates. All rights reserved.
 */
package com.junbo.common.job.cache;

import com.junbo.common.cloudant.client.CloudantUri;
import com.junbo.common.memcached.JunboMemcachedClient;
import com.junbo.configuration.ConfigService;
import com.junbo.configuration.ConfigServiceManager;
import net.spy.memcached.MemcachedClientIF;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;

/**
 * CacheSnifferJob.
 */
public class CacheSnifferJob implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheSnifferJob.class);

    private static MemcachedClientIF memcachedClient = JunboMemcachedClient.instance();
    private Integer expiration;
    private Integer maxEntitySize;

    @Override
    public void afterPropertiesSet() throws Exception {
        ConfigService configService = ConfigServiceManager.instance();

        String strMaxEntitySize = configService.getConfigValue("common.memcached.maxentitysize");
        String strExpiration = configService.getConfigValue("common.memcached.expiration");

        this.expiration = safeParseInt(strExpiration);
        this.maxEntitySize = safeParseInt(strMaxEntitySize);
    }

    private void listen() {
        List<CloudantUri> cloudantInstances = CloudantSniffer.instance().getCloudantInstances();

        for (CloudantUri cloudantUri : cloudantInstances) {
            List<String> databases = CloudantSniffer.instance().getAllDatabases(cloudantUri);

            for (String database : databases) {
                // first try to get last change token from memcached
                String lastChange = getCache(buildLastChangeKey(cloudantUri, database));

                if (StringUtils.isEmpty(lastChange)) {
                    // then try to get last change token from cloudant changes feed
                    lastChange = CloudantSniffer.instance().getLastChange(cloudantUri, database);
                }

                // listen database continuous feed
                listenDatabaseChanges(cloudantUri, database);
            }
        }
    }

    private void listenDatabaseChanges(CloudantUri cloudantUri, String database) {
        String threadName = "cloudant-listener-" + database;

        new Thread(new Runnable() {
            public void run() {
                //HttpClient httpclient = HttpClientBuilder.create().build();

            }
        }, threadName).start();
    }

    private Integer safeParseInt(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }

        return Integer.parseInt(str);
    }

    private void updateCache(String key, String value) {
        if (!checkCache(key)) {
            return;
        }

        try {
            memcachedClient.set(key, this.expiration, value).get();
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
        return uri.getValue() + "#" + isNull(uri.getUsername(), "") + "#" + database;
    }

    private String isNull(String input, String replace) {
        return input == null ? replace : input;
    }
}
