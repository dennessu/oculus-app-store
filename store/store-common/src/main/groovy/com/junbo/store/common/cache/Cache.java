/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 SilkCloud and/or its affiliates. All rights reserved.
 */
package com.junbo.store.common.cache;

/**
 * The Cache class.
 */
public interface Cache<K, V> {

    void put(K key, V value);

    V get(K key);

    String getCacheName();
}
