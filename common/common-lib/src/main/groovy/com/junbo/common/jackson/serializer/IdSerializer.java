/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.junbo.common.id.Id;
import com.junbo.common.id.util.IdUtil;
import com.junbo.common.json.ObjectMapperProvider;
import com.junbo.common.model.Link;
import com.junbo.common.util.IdFormatter;

import java.io.IOException;

/**
 * Created by minhao on 2/14/14.
 */
public class IdSerializer extends JsonSerializer<Id> {

    @Override
    public void serialize(Id value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException {
        ObjectMapper mapper = ObjectMapperProvider.instance();

        Link ref = new Link();
        if (value != null) {
            ref.setHref(IdUtil.toHref(value));
            ref.setId(IdFormatter.encodeId(value));
        }

        mapper.writeValue(jgen, ref);
    }
}
