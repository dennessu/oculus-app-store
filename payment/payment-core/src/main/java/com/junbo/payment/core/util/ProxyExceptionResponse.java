/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.core.util;

import com.junbo.langur.core.client.ClientResponseException;

import java.io.IOException;

/**
 * proxy exception response.
 */
public class ProxyExceptionResponse {
    private int status;
    private String body;

    public int getStatus() {
        return status;
    }

    public String getBody() {
        return body;
    }

    public ProxyExceptionResponse(Throwable throwable){
        if(throwable instanceof ClientResponseException){
            status = ((ClientResponseException)throwable).getResponse().getStatusCode();
            try {
                body = ((ClientResponseException)throwable).getResponse().getResponseBody();
            } catch (IOException e) {

            }
        }
    }
}
