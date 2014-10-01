/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>Resolve enumeration with its id.</p>
 * <p/>
 * <p>Sample usage please refer to <code>EnumRegistryTest</code></p>
 */
public final class EnumRegistry {
    private static ConcurrentHashMap<Class, Map<Object, Enum>> registry = new ConcurrentHashMap();

    private EnumRegistry() {
    }

    public static <T extends Enum> T resolve(Object id, Class<T> clazz) {
        if (!Identifiable.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("The enum should implement [Identifiable] interface.");
        }

        // lazy register
        if (!registry.containsKey(clazz)) {
            register(clazz);
        }

        return (T) registry.get(clazz).get(id);
    }

    public static <T> T getId(Enum e) {
        if (!(e instanceof Identifiable)) {
            throw new IllegalArgumentException("The enum should implement [Identifiable] interface.");
        }

        return (T) ((Identifiable) e).getId();
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
