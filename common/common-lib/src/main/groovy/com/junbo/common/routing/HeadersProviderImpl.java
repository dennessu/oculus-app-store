/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.routing;

import com.junbo.common.util.Context;
import com.junbo.langur.core.client.HeadersProvider;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

/**
 * The implementation of headers provider.
 */
public class HeadersProviderImpl implements HeadersProvider {
    @Override
    public MultivaluedMap<String, String> getHeaders() {
        // forward all headers
        ContainerRequestContext requestContext = Context.get().getRequestContext();
        if (requestContext != null) {
            return requestContext.getHeaders();
        }
        return new MultivaluedHashMap<>();
    }
}
