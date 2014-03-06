/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.junbo.common.jackson.model.ResourceRef;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * ResourceIdSerializer.
 */
public class ResourceIdSerializer extends JsonSerializer<Object> implements ResourceAware {
    // thread safe
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private String resourceType;

    public ResourceIdSerializer() {
    }

    @Override
    public void injectResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    @Override
    public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
        if (value == null) {
            MAPPER.writeValue(jgen, null);
            return;
        }

        Object results = isCollection(value) ? handleCollection(value) : handleSingle(value);
        MAPPER.writeValue(jgen, results);
    }

    private List<ResourceRef> handleCollection(Object value) {
        Collection collection = (Collection) value;

        List<ResourceRef> results = new ArrayList<ResourceRef>();
        Iterator it = collection.iterator();
        while (it.hasNext()) {
            results.add(handleSingle(it.next()));
        }

        return results;
    }

    private ResourceRef handleSingle(Object value) {
        ResourceRef ref = new ResourceRef();
        ref.setHref(getResourceHref(value));
        ref.setId(value.toString());

        return ref;
    }

    private boolean isCollection(Object value) {
        return value instanceof Collection;
    }

    protected String getResourceHref(Object value) {
        // determine resource url prefix with resourceType later
        // ignore the url below for now >_<
        return "http://api.oculusvr.com/v1/" + resourceType + "/" + value;
    }
}
