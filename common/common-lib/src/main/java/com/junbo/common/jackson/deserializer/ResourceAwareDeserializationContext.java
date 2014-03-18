/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.fasterxml.jackson.databind.deser.DeserializerCache;
import com.fasterxml.jackson.databind.deser.DeserializerFactory;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * ResourceAwareDeserializationContext.
 */
public class ResourceAwareDeserializationContext extends DefaultDeserializationContext {
    private static final int UNIQUE_PARAM_INDEX = 0;
    private static final int UNIQUE_GENERIC_TYPE_INDEX = 0;

    public ResourceAwareDeserializationContext() {
        super(BeanDeserializerFactory.instance, null);
    }

    protected ResourceAwareDeserializationContext(DeserializerFactory df, DeserializerCache cache) {
        super(df, cache);
    }

    protected ResourceAwareDeserializationContext(
            DefaultDeserializationContext src,
            DeserializationConfig config, JsonParser jp, InjectableValues values) {
        super(src, config, jp, values);
    }

    protected ResourceAwareDeserializationContext(DefaultDeserializationContext src, DeserializerFactory factory) {
        super(src, factory);
    }

    @Override
    public ResourceAwareDeserializationContext with(DeserializerFactory factory) {
        return new ResourceAwareDeserializationContext(this, factory);
    }

    @Override
    public ResourceAwareDeserializationContext createInstance(DeserializationConfig config,
                                                              JsonParser jp, InjectableValues values) {
        return new ResourceAwareDeserializationContext(this, config, jp, values);
    }

    @Override
    public JsonDeserializer<Object> deserializerInstance(Annotated annotated,
                                                         Object deserDef)
            throws JsonMappingException {
        JsonDeserializer<Object> deser = super.deserializerInstance(annotated, deserDef);

        if (deser instanceof ResourceCollectionAware) {
            Type propertyType;
            Class<?> propertyClass;
            if (annotated instanceof AnnotatedWithParams) {
                propertyType = ((AnnotatedWithParams) annotated).getGenericParameterType(UNIQUE_PARAM_INDEX);
                propertyClass = ((AnnotatedWithParams) annotated).getRawParameterType(UNIQUE_PARAM_INDEX);
            } else if (annotated instanceof AnnotatedMember) {
                propertyType = annotated.getGenericType();
                propertyClass = annotated.getRawType();
            } else {
                throw new JsonMappingException("ResourceAwareDeserializer does not support ["
                        + annotated.getClass().getSimpleName() + "] for now.");
            }

            Class<?> idClassType;

            // extract component type as id class type if the property is of collection type
            if (Collection.class.isAssignableFrom(propertyClass)) {
                ((ResourceCollectionAware) deser).injectCollectionType((Class<? extends Collection>) propertyClass);

                idClassType = (Class<?>) (((ParameterizedType) propertyType)
                        .getActualTypeArguments()[UNIQUE_GENERIC_TYPE_INDEX]);
            } else {
                idClassType = propertyClass;
            }

            ((ResourceCollectionAware) deser).injectIdClassType(idClassType);
        }

        return deser;
    }
}
