/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.cloudant.json.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.junbo.common.id.Id;

import java.io.IOException;

/**
 * Created by minhao on 2/14/14.
 */
public class IdCloudantSerializer extends JsonSerializer<Id> {

    @Override
    public boolean isEmpty(Id value) {
        return value == null || value.getValue() == null;
    }

    @Override
    public void serialize(Id value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeString(value.getValue().toString());
    }
}
