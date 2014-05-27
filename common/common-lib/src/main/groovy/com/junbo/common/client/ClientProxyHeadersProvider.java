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
import java.util.List;

/**
 * The implementation of headers provider used when calling client proxy.
 */
public class ClientProxyHeadersProvider implements HeadersProvider {
    private List<String> forwardedHeaders = new ArrayList<>();

    public void setForwardedHeaders(List<String> forwardedHeaders) {
        this.forwardedHeaders = forwardedHeaders;
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
        }
        return result;
    }
}
