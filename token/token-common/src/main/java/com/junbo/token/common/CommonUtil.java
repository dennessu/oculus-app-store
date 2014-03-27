/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.SerializeWriter;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.*;


/**
 * Common Util.
 */
public final class CommonUtil {
    private static final String SHA_ALG = "SHA-256";
    private static final String ENCODING = "UTF-8";

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

    //TODO: need find a better way other than truncate the hex.
    public static Long computeHash(String text){
        String hexValue = DigestUtils.sha256Hex(text);
        return Long.parseLong(hexValue.substring(0,14), 16);
    }

    //TODO:
    public static String encrypt(String text){
        return text;
    }

    //TODO:
    public static String decrypt(String text){
        return text;
    }
}
