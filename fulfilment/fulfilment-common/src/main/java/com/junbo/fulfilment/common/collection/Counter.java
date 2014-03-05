/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.common.collection;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Counter.
 */
public class Counter {
    private static final Integer ZERO = 0;

    private Map<String, Integer> map;

    public Counter() {
        map = new HashMap<>();
    }

    public Integer get(String key) {
        Integer count = map.get(key);
        return count == null ? ZERO : count;
    }

    public void count(String key, Integer value) {
        map.put(key, get(key) + value);
    }

    public Set<String> keys() {
        return map.keySet();
    }
}
