/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.clientproxy.adyen;

import com.junbo.langur.core.client.MessageTranscoder;
import com.junbo.langur.core.client.TypeReference;

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
    public <T> String encode(T body) {
        return body.toString();
    }
}
