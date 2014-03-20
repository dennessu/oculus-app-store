/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.junbo.common.jackson.common.ResourceAware;
import com.junbo.common.jackson.common.ResourceCollectionAware;
import com.junbo.common.jackson.model.ResourceRef;
import com.junbo.common.shuffle.Oculus48Id;
import junit.framework.Assert;

import java.io.IOException;
import java.util.*;

/**
 * ResourceIdDeserializer.
 */
public class ResourceIdDeserializer extends JsonDeserializer<Object> implements ResourceCollectionAware, ResourceAware {
    // thread safe
    protected static final ObjectMapper MAPPER = new ObjectMapper();

    protected Class<? extends Collection> collectionType;

    protected Class<?> idClassType;

    protected String resourcePath;

    @Override
    public void injectCollectionType(Class<? extends Collection> collectionType) {
        this.collectionType = collectionType;
    }

    @Override
    public void injectIdClassType(Class<?> idClassType) {
        this.idClassType = idClassType;
    }

    @Override
    public void injectResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    @Override
    public Object deserialize(JsonParser jsonParser, DeserializationContext context)
            throws IOException {
        Assert.assertNotNull("IdClassType", idClassType);

        return isCollection() ? handleCollection(jsonParser) : handleSingle(jsonParser);
    }

    protected Long decode(String id) {
        Oculus48Id.validateEncodedValue(id);
        return Oculus48Id.unShuffle(Oculus48Id.deFormat(id));
    }

    protected Object handleSingle(JsonParser jsonParser) throws IOException {
        ResourceRef resourceRef = MAPPER.readValue(jsonParser, ResourceRef.class);

        if (resourceRef == null) {
            return null;
        }

        return parse(resourceRef.getId(), idClassType);
    }

    protected <T> T parse(String id, Class clazz) {
        // for now, we only support String/Integer/Long id types
        if (clazz == Long.class) {
            return (T) decode(id);
        } else if (clazz == Integer.class) {
            return (T) Integer.valueOf(id);
        }

        return (T) id;
    }

    private Object handleCollection(JsonParser jsonParser) throws IOException {
        Collection<Object> results = createEmptyCollection(collectionType);
        Collection<ResourceRef> references = MAPPER.readValue(jsonParser,
                MAPPER.getTypeFactory().constructCollectionType(collectionType, ResourceRef.class));

        for (ResourceRef ref : references) {
            results.add(parse(ref.getId(), idClassType));
        }

        return results;
    }

    private boolean isCollection() {
        return collectionType != null;
    }

    private Collection<Object> createEmptyCollection(Class collectionType) {
        if (List.class.equals(collectionType)) {
            return new ArrayList<>();
        }

        if (Set.class.equals(collectionType)) {
            return new HashSet<>();
        }

        throw new IllegalStateException(
                "Unsupported collection type [" + collectionType + "] for ResourceIdDeserializer");
    }
}
