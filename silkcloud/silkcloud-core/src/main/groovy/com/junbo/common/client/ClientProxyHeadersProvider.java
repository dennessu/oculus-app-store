/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.client;

import com.junbo.langur.core.client.HeadersProvider;
import com.junbo.langur.core.context.JunboHttpContext;
import org.glassfish.jersey.internal.util.collection.StringKeyIgnoreCaseMultivaluedMap;

import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The implementation of headers provider used when calling client proxy.
 */
public class ClientProxyHeadersProvider implements HeadersProvider {
    private Map<String, String> defaultHeaders = new HashMap<>();

    private List<String> forwardedHeaders = new ArrayList<>();

    private List<String> forwardedQAHeaders = new ArrayList<>();

    private boolean forwardedQAHeadersEnabled;

    public void setDefaultHeaders(Map<String, String> defaultHeaders) {
        this.defaultHeaders = defaultHeaders;
    }

    public void setForwardedHeaders(List<String> forwardedHeaders) {
        this.forwardedHeaders = forwardedHeaders;
    }

    public void setForwardedQAHeaders(List<String> forwardedQAHeaders) {
        this.forwardedQAHeaders = forwardedQAHeaders;
    }

    public void setForwardedQAHeadersEnabled(boolean forwardedQAHeadersEnabled) {
        this.forwardedQAHeadersEnabled = forwardedQAHeadersEnabled;
    }

    @Override
    public MultivaluedMap<String, String> getHeaders() {
        MultivaluedMap<String, String> result = new StringKeyIgnoreCaseMultivaluedMap<>();
        MultivaluedMap<String, String> source = JunboHttpContext.getRequestHeaders();

        if (source != null) {
            for (String key : forwardedHeaders) {
                List<String> value = source.get(key);
                if (value != null) {
                    result.put(key, value);
                }
            }
            if (forwardedQAHeadersEnabled) {
                for (String key : forwardedQAHeaders) {
                    List<String> value = source.get(key);
                    if (value != null) {
                        result.put(key, value);
                    }
                }
            }
        }

        if (defaultHeaders != null) {
            for (String key : defaultHeaders.keySet()) {
                result.putSingle(key, defaultHeaders.get(key));
            }
        }

        return result;
    }
}
