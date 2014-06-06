/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.cloudant.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.junbo.common.cloudant.json.deserializer.EnumIdCloudantDeserializer;
import com.junbo.common.cloudant.json.deserializer.IdCloudantDeserializer;
import com.junbo.common.cloudant.json.serializer.EnumIdCloudantSerializer;
import com.junbo.common.cloudant.json.serializer.IdCloudantSerializer;
import com.junbo.common.id.util.IdUtil;
import com.junbo.common.jackson.common.CustomDeserializationContext;
import com.junbo.common.jackson.common.CustomSerializerProvider;
import com.junbo.common.jackson.deserializer.BigDecimalFromStringDeserializer;
import com.junbo.common.jackson.deserializer.LongFromStringDeserializer;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import java.math.BigDecimal;

/**
 * Created by minhao on 2/13/14.
 */
@Provider
public class CloudantObjectMapper implements ContextResolver<ObjectMapper> {

    // thread safe
    private static ObjectMapper objectMapper = createObjectMapper();

    protected static ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper(null,
                new CustomSerializerProvider(),
                new CustomDeserializationContext());

        objectMapper.setDateFormat(new ISO8601DateFormat());

        objectMapper.disable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // only serialize base on fields
        objectMapper.setVisibilityChecker(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));

        SimpleModule module = new SimpleModule(CloudantObjectMapper.class.getName());

        // pass long as string, since the json long is not full 64-bit.
        module.addSerializer(Long.class, new ToStringSerializer());
        module.addDeserializer(Long.class, new LongFromStringDeserializer());

        module.addSerializer(BigDecimal.class, new ToStringSerializer());
        module.addDeserializer(BigDecimal.class, new BigDecimalFromStringDeserializer());

        for (Class cls : IdUtil.ID_CLASSES) {
            module.addSerializer(cls, new IdCloudantSerializer());
            module.addDeserializer(cls, new IdCloudantDeserializer(cls));
        }

        for (Class cls : IdUtil.ENUM_ID_CLASSES) {
            module.addSerializer(cls, new EnumIdCloudantSerializer());
            module.addDeserializer(cls, new EnumIdCloudantDeserializer(cls));
        }

        objectMapper.registerModule(module);
        objectMapper.setAnnotationIntrospector(new CloudantAnnotationIntrospector());
        return objectMapper;
    }

    public static ObjectMapper instance() {
        return objectMapper;
    }

    public static void setInstance(ObjectMapper objectMapper) {
        CloudantObjectMapper.objectMapper = objectMapper;
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return objectMapper;
    }
}
