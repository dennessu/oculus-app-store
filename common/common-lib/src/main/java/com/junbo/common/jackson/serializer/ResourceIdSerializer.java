/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.junbo.common.jackson.common.ResourceAware;
import com.junbo.common.jackson.model.ResourceRef;
import com.junbo.common.shuffle.Oculus48Id;
import junit.framework.Assert;

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

    protected static final String RESOURCE_URL_PREFIX = "http://api.wan-san.com/v1";

    protected String resourcePath;

    public ResourceIdSerializer() {
    }

    @Override
    public void injectResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    @Override
    public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException {
        Assert.assertNotNull("resourcePath", resourcePath);

        if (unwrap(value) == null) {
            MAPPER.writeValue(jgen, null);
            return;
        }

        Object results = isCollection(value) ? handleCollection(value) : handleSingle(value);
        MAPPER.writeValue(jgen, results);
    }

    protected Object unwrap(Object value) {
        return value;
    }

    protected String encode(Object value) {
        if (value instanceof Long) {
            Oculus48Id.validateRawValue((Long) value);
            return Oculus48Id.format(Oculus48Id.shuffle((Long) value));
        } else {
            return value == null ? "" : value.toString();
        }
    }

    protected String getResourceHref(Object value) {
        return RESOURCE_URL_PREFIX + resourcePath + "/" + encode(value);
    }

    protected List<ResourceRef> handleCollection(Object value) {
        Collection collection = (Collection) value;

        List<ResourceRef> results = new ArrayList<>();
        Iterator it = collection.iterator();
        while (it.hasNext()) {
            results.add(handleSingle(it.next()));
        }

        return results;
    }

    protected ResourceRef handleSingle(Object value) {
        ResourceRef ref = new ResourceRef();
        ref.setHref(getResourceHref(value));
        ref.setId(encode(value));

        return ref;
    }

    private boolean isCollection(Object value) {
        return value instanceof Collection;
    }
}
