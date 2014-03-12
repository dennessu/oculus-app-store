/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.SerializeWriter;

import java.util.*;


/**
 * Common Util.
 */
public final class CommonUtil {

    private CommonUtil(){

    }

    public static String toJson(Object input, String[] filterProperties) {
        SerializeWriter writer = new SerializeWriter();
        JSONSerializer serializer = new JSONSerializer(writer);

        // apply filters
        if (filterProperties != null) {
            final Set<String> filters = new HashSet<String>(Arrays.asList(filterProperties));
            serializer.getPropertyFilters().add(new PropertyFilter() {
                public boolean apply(Object source, String name, Object value) {
                    return !filters.contains(name);
                }
            });
        }

        serializer.write(input);
        return writer.toString();
    }

    public static <T> T parseJson(String json, Class<T> classType){
        return JSON.parseObject(json, classType);
    }

    public static boolean toBool(String value){
        return value.equalsIgnoreCase("Yes") || value.equalsIgnoreCase("True");
    }

    public static boolean isNullOrEmpty(String value){
        return value == null || value.isEmpty();
    }

    public static <T> List<T> filter(List<T> target, IPredicate<T> predicate) {
        List<T> result = new ArrayList<T>();
        for (T element: target) {
            if (predicate.apply(element)) {
                result.add(element);
            }
        }
        return result;
    }
}
