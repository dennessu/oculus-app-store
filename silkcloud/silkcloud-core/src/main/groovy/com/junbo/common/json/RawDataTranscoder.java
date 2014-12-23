/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.json;

import com.junbo.langur.core.client.MessageTranscoder;
import com.junbo.langur.core.client.TypeReference;

import java.io.UnsupportedEncodingException;

/**
 * Raw Data Transcoder.
 */
public class RawDataTranscoder implements MessageTranscoder {

    @Override
    @SuppressWarnings("unchecked")
    public <T> T decode(TypeReference typeRef, String body) {
        if(body == null || body.isEmpty()){
            return null;
        }else{
            return (T)body;
        }
    }

    @Override
    public <T> byte[] encode(T body) {
        try {
            return body.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unable to encode: " + body, e);
        }
    }
}
