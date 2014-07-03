/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.cloudant.json;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by Zhanxin on 5/26/2014.
 */
public class PolymorphicObjectMapperProvider extends CloudantObjectMapper {

    // thread safe
    private static ObjectMapper objectMapper = createObjectMapper();

    protected static ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = CloudantObjectMapper.createObjectMapper();
        objectMapper.enableDefaultTyping();
        return objectMapper;
    }

    public static ObjectMapper instance() {
        return objectMapper;
    }
}
