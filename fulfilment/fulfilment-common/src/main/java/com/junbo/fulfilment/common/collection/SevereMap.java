/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.common.collection;

import java.util.Map;

/**
 * MapWrapper.
 *
 * @param <K> key generic type.
 * @param <V> value generic type.
 */
public class SevereMap<K, V> extends MapWrapper<K, V> {
    public SevereMap(Map<K, V> adaptee) {
        super(adaptee);
    }

    @Override
    public V get(Object key) {
        if (!containsKey(key)) {
            throw new IllegalStateException("Key [" + key + "] not found.");
        }

        return adaptee.get(key);
    }
}
