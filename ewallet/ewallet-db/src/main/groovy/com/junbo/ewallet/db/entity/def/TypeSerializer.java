/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.ewallet.db.entity.def;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.junbo.ewallet.spec.def.WalletLotType;
import com.junbo.ewallet.spec.def.WalletType;

import java.io.IOException;

/**
 * javadoc.
 */
public class TypeSerializer {
    /**
     * javadoc.
     */
    public static class WalletTypeSerializer extends JsonSerializer<WalletType> {
        @Override
        public void serialize(WalletType value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeNumber(value.getId());
        }
    }

    /**
     * javadoc.
     */
    public static class WalletLotTypeSerializer extends JsonSerializer<WalletLotType> {
        @Override
        public void serialize(WalletLotType value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeNumber(value.getId());
        }
    }
}
