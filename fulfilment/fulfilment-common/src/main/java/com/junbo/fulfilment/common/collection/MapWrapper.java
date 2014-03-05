/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.common.collection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * MapWrapper.
 *
 * @param <K> key generic type.
 * @param <V> value generic type.
 */
public abstract class MapWrapper<K, V> implements Map<K, V> {
    protected Map<K, V> adaptee;

    public MapWrapper(Map<K, V> adaptee) {
        if (adaptee == null) {
            adaptee = new HashMap<>();
        }

        this.adaptee = adaptee;
    }

    @Override
    public int size() {
        return adaptee.size();
    }

    @Override
    public boolean isEmpty() {
        return adaptee.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return adaptee.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return adaptee.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return adaptee.get(key);
    }

    @Override
    public V put(K key, V value) {
        return adaptee.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return adaptee.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        adaptee.putAll(m);
    }

    @Override
    public void clear() {
        adaptee.clear();
    }

    @Override
    public Set<K> keySet() {
        return adaptee.keySet();
    }

    @Override
    public Collection<V> values() {
        return adaptee.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return adaptee.entrySet();
    }
}
