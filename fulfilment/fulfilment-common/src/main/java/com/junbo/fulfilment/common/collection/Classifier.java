/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.common.collection;

import java.util.*;

/**
 * Classifier.
 *
 * @param <V> the type of content to be classified.
 */
public class Classifier<V> {
    private Map<String, List<V>> map;

    public Classifier() {
        map = new HashMap<>();
    }

    public void classify(String key, V value) {
        List<V> values = get(key);

        if (values == null) {
            values = new ArrayList<>();
        }

        values.add(value);
        map.put(key, values);
    }

    public List<V> get(String key) {
        return map.get(key);
    }

    public Set<String> keys() {
        return map.keySet();
    }
}
