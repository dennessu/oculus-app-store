/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.filter;

import com.junbo.common.util.Context;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import java.io.IOException;

/**
 * OverrideApiHostFilter.
 */
@PreMatching
public class CommonHeadersFilter implements ContainerRequestFilter {
    public static final String X_OVERRIDE_API_HOST = "x-override-api-host";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String apiHost = requestContext.getHeaders().getFirst(X_OVERRIDE_API_HOST);

        if (apiHost != null) {
            Context.get().putHeader(X_OVERRIDE_API_HOST, apiHost);
        }
    }
}
