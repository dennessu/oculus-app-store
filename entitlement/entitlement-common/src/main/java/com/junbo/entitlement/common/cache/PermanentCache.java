/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.entitlement.common.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * permanent cache.
 */
public enum PermanentCache {
    ENTITLEMENT_DEFINITION;

    private static final Logger LOGGER = LoggerFactory.getLogger(PermanentCache.class);
    private Cache<Object, Object> cache = CacheBuilder.newBuilder().build();

    public Object get(Object key) {
        return cache.getIfPresent(key);
    }

    public Object get(Object key, Callable<Object> loader) {
        try {
            return cache.get(key, loader);
        } catch (ExecutionException e) {
            LOGGER.error("ExecutionException during  get " + this.name() + " [" + key + "]", e);
            return null;
        } catch (CacheLoader.InvalidCacheLoadException e) {
            LOGGER.error("InvalidCacheLoadException during  get " + this.name() + " [" + key + "]", e);
            return null;
        }
    }

    public void put(Object key, Object value) {
        cache.put(key, value);
    }
}
