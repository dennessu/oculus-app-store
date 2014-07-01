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
import com.junbo.common.json.ObjectMapperProvider;
import com.junbo.common.model.SigningSupport;
import com.junbo.common.model.SignedModel;

import java.io.IOException;

/**
 * Serializer that sign the model withe the key.
 */
public class SigningSupportSerializer extends JsonSerializer<SigningSupport> {

    @Override
    public void serialize(SigningSupport value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        ObjectMapper mapper = ObjectMapperProvider.instance();

        SignedModel signedModel = new SignedModel();
        if (value != null) {
            signedModel.setPayload(value.getPayload());
            signedModel.setSignature(value.getSignature());
        }

        mapper.writeValue(jgen, signedModel);
    }
}
