/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.jackson.common;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import com.junbo.common.jackson.aware.AnnotationsAware;

/**
 * CustomSerializerProvider.
 */
public class CustomSerializerProvider extends DefaultSerializerProvider {
    public CustomSerializerProvider() {

    }

    public CustomSerializerProvider(CustomSerializerProvider src,
                                    SerializationConfig config, SerializerFactory jsf) {
        super(src, config, jsf);
    }

    @Override
    public CustomSerializerProvider createInstance(SerializationConfig config, SerializerFactory jsf) {
        return new CustomSerializerProvider(this, config, jsf);
    }

    @Override
    public JsonSerializer<Object> serializerInstance(Annotated annotated,
                                                     Object serDef)
            throws JsonMappingException {
        JsonSerializer<Object> ser = super.serializerInstance(annotated, serDef);
        if (ser instanceof AnnotationsAware) {
            ((AnnotationsAware) ser).injectAnnotations(annotated);
        }

        return ser;
    }
}
