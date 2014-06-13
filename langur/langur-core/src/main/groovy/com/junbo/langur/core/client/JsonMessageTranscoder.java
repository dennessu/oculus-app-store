/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.langur.core.client;

/**
 * Java doc.
 */
public class JsonMessageTranscoder implements MessageTranscoder {

    @Override
    public <T> T decode(TypeReference typeRef, String body) {
        return null;
    }

    @Override
    public <T> byte[] encode(T body) {
        return null;
    }
}
