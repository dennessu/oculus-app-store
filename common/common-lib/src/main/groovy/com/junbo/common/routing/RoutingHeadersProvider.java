/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.routing;

import com.junbo.langur.core.client.HeadersProvider;
import com.junbo.langur.core.context.JunboHttpContext;
import org.glassfish.jersey.internal.util.collection.StringKeyIgnoreCaseMultivaluedMap;

import javax.ws.rs.core.MultivaluedMap;

/**
 * The implementation of headers provider when the call is forwareded by router.
 */
public class RoutingHeadersProvider implements HeadersProvider {
    @Override
    public MultivaluedMap<String, String> getHeaders() {

        if (JunboHttpContext.getRequestHeaders() != null) {
            return JunboHttpContext.getRequestHeaders();
        }

        return new StringKeyIgnoreCaseMultivaluedMap<>();
    }
}
