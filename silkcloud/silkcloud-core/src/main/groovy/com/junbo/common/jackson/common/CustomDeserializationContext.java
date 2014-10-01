/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.jackson.common;

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
import com.junbo.common.jackson.aware.AnnotationsAware;
import com.junbo.common.jackson.aware.ResourceCollectionAware;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * CustomDeserializationContext.
 */
public class CustomDeserializationContext extends DefaultDeserializationContext {
    private static final int UNIQUE_PARAM_INDEX = 0;
    private static final int UNIQUE_GENERIC_TYPE_INDEX = 0;

    public CustomDeserializationContext() {
        super(BeanDeserializerFactory.instance, null);
    }

    protected CustomDeserializationContext(DeserializerFactory df, DeserializerCache cache) {
        super(df, cache);
    }

    protected CustomDeserializationContext(
            DefaultDeserializationContext src,
            DeserializationConfig config, JsonParser jp, InjectableValues values) {
        super(src, config, jp, values);
    }

    protected CustomDeserializationContext(DefaultDeserializationContext src, DeserializerFactory factory) {
        super(src, factory);
    }

    @Override
    public CustomDeserializationContext with(DeserializerFactory factory) {
        return new CustomDeserializationContext(this, factory);
    }

    @Override
    public CustomDeserializationContext createInstance(DeserializationConfig config,
                                                              JsonParser jp, InjectableValues values) {
        return new CustomDeserializationContext(this, config, jp, values);
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

        if (deser instanceof AnnotationsAware) {
            ((AnnotationsAware) deser).injectAnnotations(annotated);
        }

        return deser;
    }
}
