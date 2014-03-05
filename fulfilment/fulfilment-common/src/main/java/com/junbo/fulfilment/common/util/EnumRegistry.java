/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * EnumRegistry.
 */
public final class EnumRegistry {
    private static ConcurrentHashMap<Class, Map<Object, Enum>> registry = new ConcurrentHashMap<>();

    private EnumRegistry() {

    }

    public static <T extends Enum> T resolve(Object id, Class<T> clazz) {
        if (!Identifiable.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("The enum should implement [Identifiable] interface.");
        }

        if (!registry.containsKey(clazz)) {
            register(clazz);
        }

        return (T) registry.get(clazz).get(id);
    }

    private static <T extends Enum> void register(Class<T> clazz) {
        Map<Object, Enum> constantMap = new HashMap();

        for (T constant : clazz.getEnumConstants()) {
            Object id = ((Identifiable) constant).getId();
            constantMap.put(id, constant);
        }

        registry.putIfAbsent(clazz, constantMap);
    }
}
