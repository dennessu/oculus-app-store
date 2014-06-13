/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.junbo.langur.core.client.MessageTranscoder;
import com.junbo.langur.core.client.TypeReference;

import java.io.IOException;

/**
 * JsonMessageIgnoreUnknownTranscoder.
 */
public class JsonMessageIgnoreUnknownTranscoder implements MessageTranscoder {
    private ObjectMapper objectMapper;

    public JsonMessageIgnoreUnknownTranscoder() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Override
    public <T> T decode(TypeReference typeRef, String body) {
        if (body == null || body.isEmpty()) {
            return null;
        }

        try {
            JavaType javaType = objectMapper.getTypeFactory().constructType(typeRef.getType());
            return objectMapper.readValue(body, javaType);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Unable to decode to type " + typeRef.getType() + ": " + body, e);
        }
    }

    @Override
    public <T> byte[] encode(T body) {
        try {
            return objectMapper.writeValueAsBytes(body);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to encode: " + body, e);
        }
    }
}
