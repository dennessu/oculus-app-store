/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.junbo.common.deser.IdDeserializer;
import com.junbo.common.id.Id;
import com.junbo.common.jackson.deserializer.ResourceAwareDeserializationContext;
import com.junbo.common.jackson.serializer.ResourceAwareSerializerProvider;
import com.junbo.common.ser.IdSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static Logger logger = LoggerFactory.getLogger(ObjectMapperProvider.class);
    final ObjectMapper objectMapper;

    public ObjectMapperProvider() {
        objectMapper = new ObjectMapper(null,
                new ResourceAwareSerializerProvider(),
                new ResourceAwareDeserializationContext());

        objectMapper.setDateFormat(new ISO8601DateFormat());
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
                logger.error("Class not found exception when customize objectmapper", e);
            }
        }

        objectMapper.registerModule(module);
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return objectMapper;
    }
}
