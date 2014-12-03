/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.oauth.common.cache;

import java.util.List;

/**
 * Cache.
 */
public interface Cache {
    <V> void put(String key, V value);

    <V> V get(String key);

    <V> V get(String key, Callable<V> call);

    void evict(String key);

    void evictAll();

    List<String> getAllKeys();
}
