/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.cloudant.json.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.junbo.common.enumid.EnumId;

import java.io.IOException;

/**
 * Created by minhao on 2/14/14.
 */
public class EnumIdCloudantSerializer extends JsonSerializer<EnumId> {

    @Override
    public boolean isEmpty(EnumId value) {
        return value == null || value.getValue() == null || value.getValue().isEmpty();
    }

    @Override
    public void serialize(EnumId value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeString(value.getValue());
    }
}
