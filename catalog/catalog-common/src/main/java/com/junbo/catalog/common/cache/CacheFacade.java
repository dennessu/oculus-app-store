/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.common.cache;

import com.junbo.catalog.common.util.Callable;
import com.junbo.catalog.common.util.CloneUtils;
import com.junbo.configuration.ConfigServiceManager;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import java.util.List;

/**
 * CacheFacade.
 */
public enum CacheFacade implements Cache {
    ITEM,
    ITEM_REVISION,
    ITEM_CONTROL,
    OFFER,
    OFFER_REVISION,
    OFFER_CONTROL;

    public static boolean cacheEnabled = ConfigServiceManager.instance().getConfigValueAsBool("catalog.common.cacheEnabled", false);

    private Ehcache internal = CacheManager.getInstance().getCache(this.name());

    @Override
    public <V> void put(String key, V value) {
        if (!cacheEnabled)
            return;

        internal.put(new Element(normalize(key), CloneUtils.clone(value)));
    }

    @Override
    public <V> V get(String key) {
        if (!cacheEnabled)
            return null;

        Element element = internal.get(normalize(key));
        return element == null ? null : CloneUtils.clone((V) element.getObjectValue());
    }

    @Override
    public <V> V get(String key, Callable<V> call) {
        if (!cacheEnabled)
            return call.execute();

        Element element = internal.get(normalize(key));
        V value;
        //expired or never cached
        // reference: http://ehcache.org/documentation/recipes/cachenull
        if (element == null) {
            value = call.execute();
            put(key, CloneUtils.clone(value));
        } else {
            value = CloneUtils.clone((V)element.getObjectValue());
        }

        return value;
    }

    @Override
    public void evict(String key) {
        if (!cacheEnabled)
            return;

        internal.remove(normalize(key));
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

    private String normalize(String key) {
        return key == null ? null : key.toLowerCase();
    }
}
