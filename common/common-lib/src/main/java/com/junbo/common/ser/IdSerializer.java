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
import com.junbo.common.model.Link;
import com.junbo.common.util.IdFormatter;

import java.io.IOException;

/**
 * Created by minhao on 2/14/14.
 */
public class IdSerializer extends JsonSerializer<Id> {

    @Override
    public void serialize(Id value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        Link ref = new Link();
        if (value != null) {
            ref.setHref(getHref(value));
            ref.setId(IdFormatter.encodeId(value));
        }

        mapper.writeValue(jgen, ref);
    }

    protected String getHref(Id value) {
        // TODO: get the href template from config service and key as Id subType class
        return "http://api.oculusvr.com/v1";
    }
}
