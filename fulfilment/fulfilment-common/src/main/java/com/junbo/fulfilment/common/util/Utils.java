/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.common.util;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.SerializeWriter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMap;

import java.util.*;

/**
 * Utils.
 */
public final class Utils {
    // thread safe
    private static List<ClassMap<?, ?>> classMapList = new ArrayList<>();
    private static MapperFacade mapper = new DefaultMapperFactory.Builder().build().getMapperFacade();

    public static void registerClassMap(ClassMap<?, ?>... classMaps) {
        for (ClassMap<?, ?> classMap : classMaps) {
            classMapList.add(classMap);
        }

        // rebuild the mapper
        DefaultMapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        for (ClassMap<?, ?> classMap : classMapList) {
            mapperFactory.registerClassMap(classMap);
        }
        mapper = mapperFactory.getMapperFacade();
    }

    private Utils() {

    }

    public static Date now() {
        return new Date();
    }

    public static boolean checkString(String input) {
        return input != null && input.trim().length() > 0;
    }

    public static boolean equals(String a, String b) {
        if (a != null) {
            return a.equalsIgnoreCase(b);
        }

        return b == null;
    }

    public static String toJson(Object input) {
        return toJson(input, null);
    }

    public static String toJson(Object input, String[] filterProperties) {
        SerializeWriter writer = new SerializeWriter();
        JSONSerializer serializer = new JSONSerializer(writer);

        // apply filters
        if (filterProperties != null) {
            final Set<String> filters = new HashSet(Arrays.asList(filterProperties));
            serializer.getPropertyFilters().add(new PropertyFilter() {
                public boolean apply(Object source, String name, Object value) {
                    return !filters.contains(name);
                }
            });
        }

        serializer.write(input);
        return writer.toString();
    }

    public static <S, T> T map(S source, Class<T> targetClass) {
        return mapper.map(source, targetClass);
    }
}
