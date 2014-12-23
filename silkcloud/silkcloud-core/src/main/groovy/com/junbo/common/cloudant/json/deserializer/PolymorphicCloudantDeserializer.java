/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.cloudant.json.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.junbo.common.cloudant.json.PolymorphicObjectMapperProvider;

import java.io.IOException;

/**
 * Created by minhao on 2/14/14.
 */
public class PolymorphicCloudantDeserializer<T> extends JsonDeserializer<T> {

    private Class<T> entityClass;

    public PolymorphicCloudantDeserializer(Class<T> cls) {
        this.entityClass = cls;
    }

    @Override
    public T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = PolymorphicObjectMapperProvider.instance();
        return mapper.readValue(jp, entityClass);
    }
}
