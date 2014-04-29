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
import com.junbo.common.deser.EnumIdDeserizlizer;
import com.junbo.common.deser.IdDeserializer;
import com.junbo.common.enumid.EnumId;
import com.junbo.common.id.Id;
import com.junbo.common.jackson.common.CustomDeserializationContext;
import com.junbo.common.jackson.common.CustomSerializerModifier;
import com.junbo.common.jackson.common.CustomSerializerProvider;
import com.junbo.common.ser.EnumIdSerializer;
import com.junbo.common.ser.IdSerializer;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import java.util.Set;

/**
 * Created by minhao on 2/13/14.
 */
@Provider
public class ObjectMapperProvider implements ContextResolver<ObjectMapper> {

    // thread safe
    private static ObjectMapper objectMapper = createObjectMapper();

    private static ObjectMapper createObjectMapper() {
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

        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(true);

        provider.addIncludeFilter(new AssignableTypeFilter(Id.class));
        // scan in com.junbo.common.id package for SubClass of Id
        Set<BeanDefinition> idDefinitions = provider.findCandidateComponents("com/junbo/common/id");
        for (BeanDefinition definition : idDefinitions) {
            try {
                Class cls = Class.forName(definition.getBeanClassName());
                module.addSerializer(cls, new IdSerializer());
                module.addDeserializer(cls, new IdDeserializer(cls));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        provider.addIncludeFilter(new AssignableTypeFilter(EnumId.class));
        // scan in com.junbo.common.enumid package for Subclass of EnumId
        Set<BeanDefinition> enumIdDefinitions = provider.findCandidateComponents("com/junbo/common/enumid");
        for (BeanDefinition definition : enumIdDefinitions) {
            try {
                Class cls = Class.forName(definition.getBeanClassName());
                module.addSerializer(cls, new EnumIdSerializer());
                module.addDeserializer(cls, new EnumIdDeserizlizer(cls));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
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

    public static void setInstance(ObjectMapper objectMapper) {
        ObjectMapperProvider.objectMapper = objectMapper;
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return objectMapper;
    }
}
