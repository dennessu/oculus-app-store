/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.jackson.serializer;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import com.junbo.common.jackson.annotation.ResourceType;

/**
 * Java doc.
 */
public class ResourceAwareSerializerProvider extends DefaultSerializerProvider {
    public ResourceAwareSerializerProvider() {

    }

    public ResourceAwareSerializerProvider(ResourceAwareSerializerProvider src,
                                           SerializationConfig config, SerializerFactory jsf) {
        super(src, config, jsf);
    }

    @Override
    public ResourceAwareSerializerProvider createInstance(SerializationConfig config, SerializerFactory jsf) {
        return new ResourceAwareSerializerProvider(this, config, jsf);
    }

    @Override
    public JsonSerializer<Object> serializerInstance(Annotated annotated,
                                                     Object serDef)
            throws JsonMappingException {
        JsonSerializer<Object> ser = super.serializerInstance(annotated, serDef);
        if (ser instanceof ResourceAware) {
            ResourceType typeAnno = annotated.getAnnotation(ResourceType.class);
            if (typeAnno == null) {
                throw new IllegalStateException("ResourceType annotation is missing.");
            }
            
            ((ResourceAware) ser).injectResourceType(typeAnno.value());
        }

        return ser;
    }
}
