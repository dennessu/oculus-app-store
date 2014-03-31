/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.junbo.common.json.ObjectMapperProvider;

import java.io.Reader;

/**
 * @author dw
 */
public class JsonHelper {

    private JsonHelper() {

    }

    public static String JsonSerializer(Object obj) throws Exception {
        return GsonHelper.GsonSerializer(obj);
    }

    public static <T> T JsonDeserializer(Reader reader, Class<T> cls) throws Exception {
        ObjectMapperProvider provider = new ObjectMapperProvider();
        ObjectMapper objectMapper = provider.getContext(Object.class);
        return objectMapper.readValue(reader, cls);
    }

    /**
     * @author dw
     */
    public static class GsonHelper {

        private GsonHelper() {

        }

        public static String GsonSerializer(Object obj) throws Exception {
            Gson gson = new Gson();
            return gson.toJson(obj);
        }
    }
}
