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
import com.junbo.common.jackson.model.ResourceRef;

import java.io.IOException;
import java.util.*;

/**
 * ResourceIdDeserializer.
 */
public class ResourceIdDeserializer extends JsonDeserializer<Object> implements ResourceCollectionAware {
    private static final String ID_FIELD = "id";

    // thread safe
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private Class<? extends Collection> collectionType;

    @Override
    public void injectCollectionType(Class<? extends Collection> collectionType) {
        this.collectionType = collectionType;
    }

    @Override
    public Object deserialize(JsonParser jsonParser, DeserializationContext context)
            throws IOException {
        // for now, we assume id type is Long
        return isCollection() ? handleCollection(jsonParser) : handleSingle(jsonParser);
    }

    private Long handleSingle(JsonParser jsonParser) throws IOException {
        ResourceRef resourceRef = MAPPER.readValue(jsonParser, ResourceRef.class);

        if (resourceRef == null) {
            return null;
        }

        return Long.valueOf(resourceRef.getId());
    }

    private Object handleCollection(JsonParser jsonParser) throws IOException {
        Collection<Long> results = createEmptyCollection(collectionType);
        Collection<ResourceRef> references = MAPPER.readValue(jsonParser,
                MAPPER.getTypeFactory().constructCollectionType(collectionType, ResourceRef.class));

        for (ResourceRef ref : references) {
            results.add(Long.valueOf(ref.getId()));
        }

        return results;
    }

    private boolean isCollection() {
        return collectionType != null;
    }

    private Collection<Long> createEmptyCollection(Class collectionType) {
        if (List.class.equals(collectionType)) {
            return new ArrayList<Long>();
        }

        if (Set.class.equals(collectionType)) {
            return new HashSet<Long>();
        }

        throw new IllegalStateException(
                "Unsupported collection type [" + collectionType + "] for ResourceIdDeserializer");
    }
}
