/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.jackson.deserializer;

import com.fasterxml.jackson.databind.introspect.Annotated;
import com.junbo.common.jackson.model.ResourceRef;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CompoundIdDeserializer.
 */
public class CompoundIdDeserializer extends ResourceIdDeserializer {
    private static final Pattern FIELD_PATTERN = Pattern.compile("/?\\{([^}]*)\\}");

    private List<String> fields;
    private Pattern pathPattern;

    @Override
    public void injectAnnotations(Annotated annotations) {
        super.injectAnnotations(annotations);

        // distill fields
        fields = new ArrayList<>();
        StringBuffer pathPatternBuilder = new StringBuffer();

        // Make the fields optional
        // for example: /users(/(?<id>[^/]*))?/test-id(/(?<id>[^/]*))?
        Matcher matcher = FIELD_PATTERN.matcher(resourcePath);
        while (matcher.find()) {
            String field = matcher.group(1);
            fields.add(field);

            if (matcher.group(0).startsWith("/")) {
                matcher.appendReplacement(pathPatternBuilder, String.format("(/(?<%s>[^/]*))?", field));
            } else {
                matcher.appendReplacement(pathPatternBuilder, String.format("(?<%s>[^/]*)", field));
            }
        }
        matcher.appendTail(pathPatternBuilder);

        pathPattern = Pattern.compile(pathPatternBuilder + "$");
    }

    @Override
    protected Object process(ResourceRef resourceRef) {
        if (resourceRef == null) {
            return null;
        }

        Object result;
        try {
            result = idClassType.newInstance();
            Matcher matcher = pathPattern.matcher(resourceRef.getHref());

            if (matcher.find()) {
                for (String fieldName : fields) {
                    String fieldValue = matcher.group(fieldName);

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
