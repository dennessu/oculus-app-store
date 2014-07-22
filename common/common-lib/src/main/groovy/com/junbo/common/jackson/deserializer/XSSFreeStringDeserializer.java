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
import com.junbo.common.jackson.aware.AnnotationsAware;
import com.junbo.common.jackson.aware.ResourceCollectionAware;
import com.junbo.common.json.ObjectMapperProvider;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.*;

/**
 * XSSFreeStringDeserializer.
 */
public class XSSFreeStringDeserializer extends JsonDeserializer<Object> implements ResourceCollectionAware, AnnotationsAware {

    protected ObjectMapper mapper = ObjectMapperProvider.instance();

    protected Class<? extends Collection> collectionType;

    protected Class<?> idClassType;

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
    }

    @Override
    public Object deserialize(JsonParser jsonParser, DeserializationContext context)
            throws IOException {
        Assert.notNull(idClassType);

        return isCollection() ? handleCollection(jsonParser) : handleSingle(jsonParser);
    }

    protected Whitelist getWhitelist() {
        return Whitelist.none();
    }

    protected Object process(String value) {
        return Jsoup.clean(value, getWhitelist());
    }

    private Object handleSingle(JsonParser jsonParser) throws IOException {
        String value = jsonParser.getValueAsString();
        return process(value);
    }

    private Object handleCollection(JsonParser jsonParser) throws IOException {
        Collection<Object> results = createEmptyCollection(collectionType);
        Collection<String> references = mapper.readValue(jsonParser,
                mapper.getTypeFactory().constructCollectionType(collectionType, String.class));

        for (String ref : references) {
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
