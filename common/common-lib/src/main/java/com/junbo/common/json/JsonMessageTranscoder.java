/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.junbo.langur.core.client.MessageTranscoder;
import com.junbo.langur.core.client.TypeReference;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

/**
 * Created by minhao on 2/17/14.
 */
public class JsonMessageTranscoder implements MessageTranscoder {

    ObjectMapperProvider provider = new ObjectMapperProvider();

    @Override
    @SuppressWarnings("unchecked")
    public <T> T decode(TypeReference typeRef, String body) {
        try {
            ObjectMapper objectMapper = provider.getContext(Object.class);

            // when typeRef is a ParameterizedType Such as List<User>
            // we need to construct a collection type for jackson object mapper
            if (typeRef.getType() instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType)typeRef.getType();
                return objectMapper.readValue(body, objectMapper.getTypeFactory().
                        constructCollectionType((Class<Collection>) parameterizedType.getRawType(),
                                (Class<?>) parameterizedType.getActualTypeArguments()[0]));
            }
            else {
                return objectMapper.readValue(body, (Class<T>) typeRef.getType());
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> String encode(T body) {
        try {
            return provider.getContext(Object.class).writeValueAsString(body);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
