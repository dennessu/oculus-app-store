/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.junbo.common.deser.IdDeserializer;
import com.junbo.common.id.Id;
import com.junbo.common.jackson.deserializer.ResourceAwareDeserializationContext;
import com.junbo.common.jackson.serializer.ResourceAwareSerializerProvider;
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
    
    private final ObjectMapper objectMapper;

    public ObjectMapperProvider() {
        objectMapper = new ObjectMapper(null,
                new ResourceAwareSerializerProvider(),
                new ResourceAwareDeserializationContext());

        objectMapper.setDateFormat(new ISO8601DateFormat());

        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        SimpleModule module = new SimpleModule(getClass().getName(), new Version(1, 0, 0, null));

        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(true);
        provider.addIncludeFilter(new AssignableTypeFilter(Id.class));

        // scan in com.junbo.common.id package for SubClass of Id
        Set<BeanDefinition> components = provider.findCandidateComponents("com/junbo/common/id");
        for (BeanDefinition component : components) {
            try {
                Class cls = Class.forName(component.getBeanClassName());
                module.addSerializer(cls, new IdSerializer());
                module.addDeserializer(cls, new IdDeserializer(cls));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        objectMapper.registerModule(module);

        SimpleFilterProvider filterProvider = new SimpleFilterProvider();
        filterProvider.addFilter(PropertyAssignedAwareFilter.class.getName(), new PropertyAssignedAwareFilter());
        objectMapper.setFilters(filterProvider);
        
        objectMapper.setAnnotationIntrospector(new PropertyAssignedAwareIntrospector());
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return objectMapper;
    }
}
