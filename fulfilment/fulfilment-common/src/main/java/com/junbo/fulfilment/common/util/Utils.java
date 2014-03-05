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

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Utils.
 */
public final class Utils {
    // thread safe
    private static final MapperFacade MAPPER = new DefaultMapperFactory.Builder().build().getMapperFacade();

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
        return MAPPER.map(source, targetClass);
    }
}
