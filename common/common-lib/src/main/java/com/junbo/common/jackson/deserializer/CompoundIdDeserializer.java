/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.junbo.common.jackson.model.ResourceRef;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CompoundIdDeserializer.
 */
public class CompoundIdDeserializer extends ResourceIdDeserializer {
    private static final Pattern FIELD_PATTERN = Pattern.compile("\\{(.*?)\\}");

    private List<String> fields;
    private Pattern pathPattern;

    @Override
    public void injectResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;

        // distill fields
        fields = new ArrayList<>();
        Matcher matcher = FIELD_PATTERN.matcher(resourcePath);
        while (matcher.find()) {
            fields.add(matcher.group(1));
        }

        // prepare resource path pattern
        pathPattern = Pattern.compile(resourcePath.replaceAll("\\{(.*?)}", "(.*?)") + "$");
    }

    protected Object handleSingle(JsonParser jsonParser) throws IOException {
        ResourceRef resourceRef = MAPPER.readValue(jsonParser, ResourceRef.class);

        if (resourceRef == null) {
            return null;
        }

        Object result;
        try {
            result = idClassType.newInstance();
            Matcher matcher = pathPattern.matcher(resourceRef.getHref());

            if (matcher.find()) {
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    String fieldValue = matcher.group(i);

                    String fieldName = fields.get(i - 1);

                    Field field = result.getClass().getDeclaredField(fieldName);
                    field.setAccessible(true);
                    field.set(result, parse(fieldValue, field.getType()));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error occurred during deserializering CompoundId. ");
        }

        return result;
    }
}
