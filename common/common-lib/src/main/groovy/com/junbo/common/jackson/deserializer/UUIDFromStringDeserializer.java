/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.UUID;

/**
 * LongFromStringDeserializer.
 */
public class UUIDFromStringDeserializer extends JsonDeserializer<UUID> {

    @Override
    public UUID deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        String value = jsonParser.getValueAsString();
        if (value == null || value.length() == 0) {
            return null;
        }
        return UUID.fromString(jsonParser.getValueAsString());
    }
}
