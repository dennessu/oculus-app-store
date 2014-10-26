/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.filter;

import com.junbo.common.util.Context;
import com.junbo.langur.core.profiling.ProfilingHelper;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import java.io.IOException;

/**
 * ContextFilter.
 */
public class ContextFilter implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // request filter, clear context
        Context.clear();
        Context.get().setIsInitialRestCallBeforeClear(true);
        Context.get().setIsInitialRestCall(true);

        // clear profiling info
        ProfilingHelper.clear();
    }
}
