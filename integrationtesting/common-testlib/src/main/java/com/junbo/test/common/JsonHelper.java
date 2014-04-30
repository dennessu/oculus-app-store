/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.junbo.common.json.ObjectMapperProvider;

import java.io.Reader;

/**
 * @author dw
 */
public class JsonHelper {

    private JsonHelper() {

    }

    public static String JsonSerializer(Object obj) throws Exception {
        ObjectMapperProvider provider = new ObjectMapperProvider();
        ObjectMapper objectMapper = provider.getContext(Object.class);
        return objectMapper.writeValueAsString(obj);
    }

    public static <T> T JsonDeserializer(Reader reader, Class<T> cls) throws Exception {
        ObjectMapperProvider provider = new ObjectMapperProvider();
        ObjectMapper objectMapper = provider.getContext(Object.class);
        return objectMapper.readValue(reader, cls);
    }
}
