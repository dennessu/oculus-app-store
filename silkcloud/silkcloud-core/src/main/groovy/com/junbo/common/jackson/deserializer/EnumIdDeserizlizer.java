/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.junbo.common.enumid.EnumId;
import com.junbo.common.error.AppCommonErrors;
import com.junbo.common.json.ObjectMapperProvider;
import com.junbo.common.model.Link;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by haomin on 14-4-24.
 * @param <T>
 */
public class EnumIdDeserizlizer<T extends EnumId> extends JsonDeserializer<T> {
    private static Logger logger = LoggerFactory.getLogger(IdDeserializer.class);
    private Class<T> clazz;

    public EnumIdDeserizlizer(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonToken t = jp.getCurrentToken();

        ObjectMapper mapper = ObjectMapperProvider.instance();
        Link ref = mapper.readValue(jp, Link.class);

        T id;
        try {
            id = this.clazz.newInstance();
            if (ref != null) {
                if (StringUtils.isEmpty(ref.getId())) {
                    throw AppCommonErrors.INSTANCE.fieldInvalid(jp.getCurrentName()).exception();
                }
                id.setValue(ref.getId());
            }
        }
        catch (InstantiationException | IllegalAccessException e) {
            logger.error("Resource Id deserialization failed", e);
            throw ctxt.mappingException(clazz, t);
        }

        return id;
    }
}
