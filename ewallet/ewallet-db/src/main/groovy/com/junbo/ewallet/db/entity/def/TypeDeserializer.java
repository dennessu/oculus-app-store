/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.ewallet.db.entity.def;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.junbo.common.util.EnumRegistry;
import com.junbo.ewallet.spec.def.WalletLotType;
import com.junbo.ewallet.spec.def.WalletType;

import java.io.IOException;

/**
 * javadoc.
 */
public class TypeDeserializer {
    /**
     * javadoc.
     */
    public static class WalletTypeDeserializer extends JsonDeserializer<WalletType> {
        @Override
        public WalletType deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            Integer value = jp.getValueAsInt();
            return EnumRegistry.resolve(value, WalletType.class);
        }
    }

    /**
     * javadoc.
     */
    public static class WalletLotTypeDeserializer extends JsonDeserializer<WalletLotType> {
        @Override
        public WalletLotType deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            Integer value = jp.getValueAsInt();
            return EnumRegistry.resolve(value, WalletLotType.class);
        }
    }
}
