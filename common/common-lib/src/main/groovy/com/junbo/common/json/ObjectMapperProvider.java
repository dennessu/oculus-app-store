/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.junbo.common.id.util.IdUtil;
import com.junbo.common.jackson.common.CustomDeserializationContext;
import com.junbo.common.jackson.common.CustomSerializerModifier;
import com.junbo.common.jackson.common.CustomSerializerProvider;
import com.junbo.common.jackson.deserializer.CloudantIdDeserializer;
import com.junbo.common.jackson.deserializer.EnumIdDeserizlizer;
import com.junbo.common.jackson.serializer.CloudantIdSerializer;
import com.junbo.common.jackson.serializer.EnumIdSerializer;
import com.junbo.common.jackson.serializer.IdSerializer;
import com.junbo.common.jackson.deserializer.IdDeserializer;
import com.junbo.common.jackson.serializer.SigningSupportSerializer;
import com.junbo.common.model.SigningSupport;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

/**
 * Created by minhao on 2/13/14.
 */
@Provider
public class ObjectMapperProvider implements ContextResolver<ObjectMapper> {

    // thread safe
    private static ObjectMapper objectMapper = createObjectMapper();

    private static ObjectMapper objectMapperNoSigningSupport = createObjectMapper(false);

    private static ObjectMapper createObjectMapper() {
        return createObjectMapper(true);
    }

    private static ObjectMapper createObjectMapper(boolean supportSign) {
        ObjectMapper objectMapper = new ObjectMapper(null,
                new CustomSerializerProvider(),
                new CustomDeserializationContext());

        objectMapper.setDateFormat(new ISO8601DateFormat());

        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

        SimpleModule module = new SimpleModule(ObjectMapperProvider.class.getName()) {
            public void setupModule(SetupContext context) {
                super.setupModule(context);
                context.addBeanSerializerModifier(new CustomSerializerModifier());
            }
        };

        for (Class cls : IdUtil.ID_CLASSES) {
            module.addSerializer(cls, new IdSerializer());
            module.addDeserializer(cls, new IdDeserializer(cls));
        }

        for (Class cls : IdUtil.CLOUDANT_ID_CLASSES) {
            module.addSerializer(cls, new CloudantIdSerializer());
            module.addDeserializer(cls, new CloudantIdDeserializer(cls));
        }

        for (Class cls : IdUtil.ENUM_ID_CLASSES) {
            module.addSerializer(cls, new EnumIdSerializer());
            module.addDeserializer(cls, new EnumIdDeserizlizer(cls));
        }

        if (supportSign) {
            module.addSerializer(SigningSupport.class, new SigningSupportSerializer());
        }

        objectMapper.registerModule(module);

        SimpleFilterProvider filterProvider = new SimpleFilterProvider();
        filterProvider.addFilter(PropertyAssignedAwareFilter.class.getName(), new PropertyAssignedAwareFilter());
        objectMapper.setFilters(filterProvider);

        objectMapper.setAnnotationIntrospector(new PropertyAssignedAwareIntrospector());
        return objectMapper;
    }

    public static ObjectMapper instance() {
        return objectMapper;
    }

    public static ObjectMapper instanceNoSigningSupport() {
        return objectMapperNoSigningSupport;
    }

    public static void setInstance(ObjectMapper objectMapper) {
        ObjectMapperProvider.objectMapper = objectMapper;
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return objectMapper;
    }
}
