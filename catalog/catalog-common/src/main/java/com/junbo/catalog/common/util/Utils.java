/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.junbo.common.shuffle.Oculus48Id;

import java.util.Date;

/**
 * Utility class.
 */
public class Utils {
    private Utils() {
    }

    public static Date now() {
        return new Date();
    }

    public static Long currentTimestamp() {
        return System.currentTimeMillis();
    }

    public static Date maxDate() {
        return new Date(253373469595309L);
    }

    public static Date minDate() {
        return new Date(0);
    }

    public static String toJson(Object input) {
        return JSON.toJSONString(input);
    }

    public static String toJsonWithType(Object input) {
        return JSON.toJSONString(input, SerializerFeature.WriteClassName);
    }

    public static <T> T fromJson(String string, Class<T> clazz){
        return JSON.parseObject(string, clazz);
    }

    public static String encodeId(Long id) {
        Oculus48Id.validateRawValue(id);
        return Oculus48Id.format(Oculus48Id.shuffle(id));
    }

    public static Long safeParseUserId(String userId) {
        try {
            return Long.parseLong(userId);
        } catch (NumberFormatException e) {
            return Long.parseLong(Constants.DEFAULT_USER_ID);
        }
    }
}
