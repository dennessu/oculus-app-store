/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.clientproxy.utils;

import com.junbo.langur.core.client.ResponseHandler;
import com.junbo.langur.core.context.JunboHttpContext;
import com.ning.http.client.Response;

import javax.ws.rs.core.MultivaluedMap;
import java.util.HashSet;
import java.util.Set;

/**
 * The StoreClientProxyResponseHandler class.
 */
public class StoreClientProxyResponseHandler implements ResponseHandler{

    private Set<String> forwardedResponseHeaders = new HashSet<>();

    public void setForwardedResponseHeaders(Set<String> forwardedResponseHeaders) {
        this.forwardedResponseHeaders = forwardedResponseHeaders;
    }

    @Override
    public void onResponse(Response response) {
        if (response == null || response.getHeaders() == null) {
            return;
        }
        MultivaluedMap<String, String> responseHeaders = JunboHttpContext.getResponseHeaders();

        for (String key : forwardedResponseHeaders) {
            String newValue = response.getHeader(key);
            if (newValue != null) {
                responseHeaders.putSingle(key, newValue);
            }
        }
    }
}
