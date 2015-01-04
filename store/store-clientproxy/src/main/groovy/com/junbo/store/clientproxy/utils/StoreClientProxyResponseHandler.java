/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.clientproxy.utils;

import com.junbo.langur.core.client.ResponseHandler;
import com.junbo.langur.core.context.JunboHttpContext;
import com.ning.http.client.Response;
import org.springframework.util.CollectionUtils;

import javax.ws.rs.core.MultivaluedMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The StoreClientProxyResponseHandler class.
 */
public class StoreClientProxyResponseHandler implements ResponseHandler{

    private Set<String> overrideHeaders = new HashSet<>();

    public void setOverrideHeaders(Set<String> overrideHeaders) {
        this.overrideHeaders = overrideHeaders;
    }

    @Override
    public void onResponse(Response response) {
        if (response == null || response.getHeaders() == null) {
            return;
        }
        MultivaluedMap<String, String> requestHeaders = JunboHttpContext.getRequestHeaders();

        for (Map.Entry<String, List<String>> entry : response.getHeaders().entrySet()) {
            if (!overrideHeaders.contains(entry.getKey()) || CollectionUtils.isEmpty(entry.getValue())) {
                continue;
            }
            requestHeaders.put(entry.getKey(), entry.getValue());
        }
    }
}
