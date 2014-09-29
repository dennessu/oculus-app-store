/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.cloudant.json.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.junbo.common.cloudant.json.PolymorphicObjectMapperProvider;

import java.io.IOException;

/**
 * Created by minhao on 2/14/14.
 */
public class PolymorphicCloudantSerializer extends JsonSerializer<Object> {

    @Override
    public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        ObjectMapper mapper = PolymorphicObjectMapperProvider.instance();
        mapper.writeValue(jgen, value);
    }
}
