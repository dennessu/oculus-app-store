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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * Created by minhao on 2/14/14.
 */
public class IdSerializer extends JsonSerializer<Id> {
    private static final String SELF_HREF_PREFIX = "http://api.oculusvr.com/v1";
    private static Logger logger = LoggerFactory.getLogger(IdSerializer.class);

    @Override
    public void serialize(Id value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        IdResourcePath pathAnno = AnnotationUtils.findAnnotation(value.getClass(), IdResourcePath.class);
        if (pathAnno == null) {
            logger.warn("IdResourcePath annotation missing on class: [" + value.getClass().getCanonicalName()+"]");
        }

        Link ref = new Link();
        if (value != null) {
            ref.setHref(getHref(value, pathAnno == null ? "/resources/{0}" : pathAnno.value()));
            ref.setId(IdFormatter.encodeId(value));
        }

        mapper.writeValue(jgen, ref);
    }

    protected String getHref(Id value, String path) {
        String href = this.formatIndexPlaceHolder(SELF_HREF_PREFIX + path, new String[]{IdFormatter.encodeId(value)});
        href = this.formatPropertyPlaceHolder(href, value.getResourcePathPlaceHolder());
        return href;
    }

    private String formatIndexPlaceHolder(String pattern, Object[] args) {
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

    private String formatPropertyPlaceHolder(String pattern, Properties properties) {
        if (pattern == null) {
            return null;
        }

        if (properties == null || properties.size() == 0) {
            return pattern;
        }

        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String key = (String)entry.getKey();
            Object value = entry.getValue();
            String encodedValue;

            if (value instanceof Id) {
                encodedValue = IdFormatter.encodeId((Id)value);
            }
            else {
                encodedValue = value.toString();
            }

            pattern = pattern.replace("{"+key+"}", encodedValue);
        }

        return pattern;
    }
}
