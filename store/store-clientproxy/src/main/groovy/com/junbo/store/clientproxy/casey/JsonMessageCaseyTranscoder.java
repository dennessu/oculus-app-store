/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.clientproxy.casey;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.junbo.common.json.ObjectMapperProvider;
import com.junbo.langur.core.client.MessageTranscoder;
import com.junbo.langur.core.client.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * The JsonMessageCaseyTranscoder class.
 */
@Component("store.jsonMessageCaseyTranscoder")
public class JsonMessageCaseyTranscoder implements MessageTranscoder {

    private ObjectMapper objectMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonMessageCaseyTranscoder.class);

    @Resource(name = "store.caseResponseValidator")
    private CaseResponseValidator caseResponseValidator;

    public JsonMessageCaseyTranscoder() {
        objectMapper = ObjectMapperProvider.createObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    @Override
    public <T> T decode(TypeReference typeRef, String body) {
        if (body == null || body.isEmpty()) {
            LOGGER.warn("name=Empty_Case_Response_Found");
            return null;
        }

        try {
            JavaType javaType = objectMapper.getTypeFactory().constructType(typeRef.getType());
            T val = objectMapper.readValue(body, javaType);
            String validateFailMsg = caseResponseValidator.validate(val);
            if (validateFailMsg != null) {
                LOGGER.warn("name=Bad_Response_From_Casey, validateMsg={}\n, response:\n{}\n", validateFailMsg, body);
            }
            return val;
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
