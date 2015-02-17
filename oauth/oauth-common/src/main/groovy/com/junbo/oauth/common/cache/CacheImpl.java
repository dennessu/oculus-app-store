/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.oauth.common.cache;

import com.junbo.configuration.ConfigServiceManager;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;

/**
 * CacheImpl.
 */
public class CacheImpl implements Cache, InitializingBean {

    public static boolean cacheEnabled = ConfigServiceManager.instance().getConfigValueAsBool("oauth.common.cacheEnabled", false);

    private Ehcache internal;

    private CacheManager cacheManager;

    private String name;

    @Required
    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Required
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public <V> void put(String key, V value) {
        if (!cacheEnabled)
            return;

        internal.put(new Element(key, value));
    }

    @Override
    public <V> V get(String key) {
        if (!cacheEnabled)
            return null;

        Element element = internal.get(key);
        return element == null ? null : (V)element.getObjectValue();
    }

    @Override
    public <V> V get(String key, Callable<V> call) {
        if (!cacheEnabled)
            return call.execute();

        Element element = internal.get(key);
        V value;
        //expired or never cached
        // reference: http://ehcache.org/documentation/recipes/cachenull
        if (element == null) {
            value = call.execute();
            put(key, value);
        } else {
            value = (V)element.getObjectValue();
        }

        return value;
    }

    @Override
    public void evict(String key) {
        if (!cacheEnabled)
            return;

        internal.remove(key);
    }

    @Override
    public void evictAll() {
        if (!cacheEnabled)
            return;

        internal.removeAll();
    }

    @Override
    public List<String> getAllKeys() {
        return internal.getKeys();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        internal = cacheManager.getCache(name);
    }
}
