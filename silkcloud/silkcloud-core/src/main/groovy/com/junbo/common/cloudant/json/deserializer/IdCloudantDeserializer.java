/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.cloudant.json.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.junbo.common.id.Id;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Java doc for IdDeserializer.
 * @param <T>
 */
public class IdCloudantDeserializer<T extends Id>
        extends JsonDeserializer<T> {

    private static Logger logger = LoggerFactory.getLogger(IdCloudantDeserializer.class);
    private Class<T> clazz;

    public IdCloudantDeserializer(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonToken t = jp.getCurrentToken();

        String value = jp.getValueAsString();
        if (value == null || value.length() == 0) {
            return null;
        }

        T id;
        try {
            Long val = Long.parseLong(value);
            id = this.clazz.newInstance();
            id.setValue(val);
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("Resource Id deserialization failed", e);
            throw ctxt.mappingException(clazz, t);
        }

        return id;
    }
}
