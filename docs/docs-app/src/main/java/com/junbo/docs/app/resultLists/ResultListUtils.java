/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.docs.app.resultlists;

import com.junbo.common.model.Results;
import org.reflections.Reflections;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The utility functions for creating non-generic result list.
 */
public class ResultListUtils {
    private ResultListUtils() {}

    /**
     * Find the non-generic ResultList type.
     */
    public static Class getClass(ParameterizedType type) {
        Type actualType = type.getActualTypeArguments()[0];
        return resultListMap.get(actualType);
    }

    private static Map<Class, Class> initializeMap() {
        Map<Class, Class> resultListMap = new HashMap<>();

        Reflections reflections = new Reflections("com.junbo.docs.app.resultlists");
        Set<Class<? extends Results>> allClasses = reflections.getSubTypesOf(Results.class);
        for (Class clazz : allClasses) {
            addToMap(clazz, resultListMap);
        }
        return resultListMap;
    }

    private static void addToMap(Class cls, Map<Class, Class> resultListMap) {
        ParameterizedType superClassType = (ParameterizedType)cls.getGenericSuperclass();
        Class valueClass = (Class)superClassType.getActualTypeArguments()[0];
        if (resultListMap.containsKey(valueClass)) {
            // duplicated result list type
            assert(false);
        }
        resultListMap.put(valueClass, cls);
    }

    /**
     * Create a map for T -> TResultList.
     */
    private static Map<Class, Class> resultListMap = initializeMap();
}
