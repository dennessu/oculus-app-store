/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.junbo.common.id.Id;
import com.junbo.common.id.IdResourcePath;
import com.junbo.common.model.Link;
import com.junbo.common.util.IdFormatter;
import org.springframework.core.annotation.AnnotationUtils;

import java.io.IOException;

/**
 * Created by minhao on 2/14/14.
 */
public class IdSerializer extends JsonSerializer<Id> {
    protected static final String SELF_HREF_PREFIX = "http://api.oculusvr.com/v1";

    @Override
    public void serialize(Id value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        IdResourcePath pathAnno = AnnotationUtils.findAnnotation(value.getClass(), IdResourcePath.class);
        Link ref = new Link();
        if (value != null) {
            ref.setHref(getHref(value, pathAnno == null ? "" : pathAnno.value()));
            ref.setId(IdFormatter.encodeId(value));
        }

        mapper.writeValue(jgen, ref);
    }

    protected String getHref(Id value, String path) {
        return this.formatMessage(SELF_HREF_PREFIX + path, new String[]{IdFormatter.encodeId(value)});
    }

    private String formatMessage(String pattern, Object[] args) {
        if (pattern == null) {
            return null;
        }

        int index = 0;
        for (Object arg : args) {
            pattern = pattern.replace("{"+index+"}", arg.toString());
            index++;
        }
        return pattern;
    }
}
