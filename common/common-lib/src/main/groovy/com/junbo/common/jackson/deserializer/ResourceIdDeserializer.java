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
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.junbo.common.jackson.annotation.ResourcePath;
import com.junbo.common.jackson.aware.AnnotationsAware;
import com.junbo.common.jackson.aware.ResourceCollectionAware;
import com.junbo.common.jackson.model.ResourceRef;
import com.junbo.common.json.ObjectMapperProvider;
import com.junbo.common.shuffle.Oculus48Id;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.*;

/**
 * ResourceIdDeserializer.
 */
public class ResourceIdDeserializer extends JsonDeserializer<Object> implements ResourceCollectionAware, AnnotationsAware {

    protected ObjectMapper mapper = ObjectMapperProvider.instance();

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
    public void injectAnnotations(Annotated annotations) {
        ResourcePath typeAnno = annotations.getAnnotation(ResourcePath.class);
        if (typeAnno == null) {
            throw new IllegalStateException("ResourcePath annotation is missing.");
        }
        this.resourcePath = typeAnno.value();
    }

    @Override
    public Object deserialize(JsonParser jsonParser, DeserializationContext context)
            throws IOException {
        Assert.notNull(idClassType);

        return isCollection() ? handleCollection(jsonParser) : handleSingle(jsonParser);
    }

    protected Long decode(String id) {
        Oculus48Id.validateEncodedValue(id);
        return Oculus48Id.unShuffle(Oculus48Id.deFormat(id));
    }

    protected <T> T parse(String id, Class clazz) {
        if (id == null || id.trim().length() == 0) {
            return null;
        }

        // for now, we only support String/Integer/Long id types
        if (clazz == Long.class) {
            return (T) decode(id);
        } else if (clazz == Integer.class) {
            return (T) Integer.valueOf(id);
        } else if (clazz == String.class) {
            return (T) id;
        }

        return (T) id;
    }

    protected Object process(ResourceRef resourceRef) {
        if (resourceRef == null) {
            return null;
        }

        return parse(resourceRef.getId(), idClassType);
    }

    private Object handleSingle(JsonParser jsonParser) throws IOException {
        ResourceRef resourceRef = mapper.readValue(jsonParser, ResourceRef.class);

        return process(resourceRef);
    }

    private Object handleCollection(JsonParser jsonParser) throws IOException {
        Collection<Object> results = createEmptyCollection(collectionType);
        Collection<ResourceRef> references = mapper.readValue(jsonParser,
                mapper.getTypeFactory().constructCollectionType(collectionType, ResourceRef.class));

        for (ResourceRef ref : references) {
            results.add(process(ref));
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
