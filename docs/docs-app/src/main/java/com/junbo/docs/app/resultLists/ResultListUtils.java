/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.docs.app.resultlists;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

/**
 * The utility functions for creating non-generic result list.
 */
public class ResultListUtils {
    private ResultListUtils() {}

    /**
     * Create a map for T -> TResultList.
     */
    public static Map<Class, Class> getMap(Class... resultListClasses) {
        Map<Class, Class> resultListMap = new HashMap<>();
        for (Class cls : resultListClasses) {
            addToMap(cls, resultListMap);
        }
        return resultListMap;
    }

    /**
     * Add the result list type to an existing map.
     */
    public static void addToMap(Class cls, Map<Class, Class> resultListMap) {
        ParameterizedType superClassType = (ParameterizedType)cls.getGenericSuperclass();
        Class valueClass = (Class)superClassType.getActualTypeArguments()[0];
        if (resultListMap.containsKey(valueClass)) {
            // duplicated result list type
            assert(false);
        }
        resultListMap.put(valueClass, cls);
    }
}
