/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.routing;

import com.junbo.langur.core.client.ResponseHandler;
import com.junbo.langur.core.context.JunboHttpContext;
import com.ning.http.client.Response;

import javax.ws.rs.core.MultivaluedMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The implementation of headers provider when the call is forwareded by router.
 */
public class RoutingResponseHandler implements ResponseHandler {
    private Set<String> blacklistHeaders = new HashSet<>();

    public void setBlacklistHeaders(Set<String> blacklistHeaders) {
        this.blacklistHeaders = blacklistHeaders;
    }

    @Override
    public void onResponse(Response response) {
        MultivaluedMap<String, String> responseHeaders = JunboHttpContext.getResponseHeaders();

        for (Map.Entry<String, List<String>> entry : response.getHeaders().entrySet()) {
            if (blacklistHeaders.contains(entry.getKey())) {
                continue;
            }
            responseHeaders.put(entry.getKey(), entry.getValue());
        }
        JunboHttpContext.setResponseStatus(response.getStatusCode());
    }
}
