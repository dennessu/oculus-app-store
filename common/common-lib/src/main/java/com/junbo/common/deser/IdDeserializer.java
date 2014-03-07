/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.junbo.common.id.Id;
import com.junbo.common.model.Reference;
import com.junbo.common.util.IdFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Java doc for IdDeserializer.
 * @param <T>
 */
public class IdDeserializer<T extends Id>
        extends JsonDeserializer<T> {

    private static Logger logger = LoggerFactory.getLogger(IdDeserializer.class);
    private Class<T> clazz;

    public IdDeserializer(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Reference ref = mapper.readValue(jp, Reference.class);

        T id = null;
        try {
            id = this.clazz.newInstance();
            if (ref != null) {
                id.setValue(IdFormat.decodeFormattedId(this.clazz, ref.getId()));
            }
        }
        catch (InstantiationException e) {
            logger.error("Resource Id deserialization failed", e);
        }
        catch (IllegalAccessException e) {
            logger.error("Resource Id deserialization failed", e);
        }

        return id;
    }
}
