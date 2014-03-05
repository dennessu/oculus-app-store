/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db.dao.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Json Marshaller used to marshall and unmarshall Object.
 */
public class JsonMarshaller {
    private JsonMarshaller() {
    }

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String marshall(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T unmarshall(Class<T> tClass, String value) {
        try {
            return objectMapper.readValue(value, tClass);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
