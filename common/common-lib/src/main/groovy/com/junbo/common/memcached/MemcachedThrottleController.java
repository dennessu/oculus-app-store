/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.memcached;

import com.junbo.common.error.AppCommonErrors;
import com.junbo.langur.core.context.JunboHttpContext;
import com.junbo.langur.core.rest.ThrottleController;
import groovy.transform.CompileStatic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * MemcachedThrottleController.
 */
@CompileStatic
public class MemcachedThrottleController implements ThrottleController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MemcachedThrottleController.class);

    private JunboMemcachedClient memcachedClient;

    private boolean enabled;

    private int defaultWindowInSeconds;

    private Map<String, Long> apiThrottleLimits;

    public MemcachedThrottleController() {
        memcachedClient = JunboMemcachedClient.instance();
    }

    @Required
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Required
    public void setDefaultWindowInSeconds(int defaultWindowInSeconds) {
        this.defaultWindowInSeconds = defaultWindowInSeconds;
    }

    @Required
    public void setApiThrottleLimits(Map<String, Long> apiThrottleLimits) {
        this.apiThrottleLimits = apiThrottleLimits;
    }

    @Override
    public void throttle(String apiName) {

        if (memcachedClient == null || !enabled || !apiThrottleLimits.containsKey(apiName)) {
            return;
        }

        String key = apiName + ":" + JunboHttpContext.getRequestIpAddress();
        Long limit = apiThrottleLimits.get(apiName);

        // Try to increase the current api count for this api + request ip
        long currentValue = memcachedClient.incr(key, 1L);
        // return value = -1 means the key does not exist
        if (currentValue == -1) {
            try {
                // Try to add key with value 1
                // There is a bug in spymemcached client(spymemcached bug 41),
                // we need to add a string value of "1" at the first time for further increment operation
                Boolean result = memcachedClient.add(key, defaultWindowInSeconds, "1").get();

                // If the add result is false, means the add operation fails, this key has already been added by other
                // thread, just increase the current api count again.
                if (!result) {
                    currentValue = memcachedClient.incr(key, 1L);
                }
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error("Error calling memcached client", e);
            }
        }

        // Check if the api count exceeds the api throttle limit
        if (currentValue > limit) {
            throw AppCommonErrors.INSTANCE.forbiddenWithMessage("The requester exceeds the api throttle limit")
                    .exception();
        }
    }
}
