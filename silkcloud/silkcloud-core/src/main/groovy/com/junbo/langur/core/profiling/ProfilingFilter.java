/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.profiling;

import com.junbo.langur.core.context.JunboHttpContext;
import com.junbo.langur.core.context.JunboHttpContextScopeListener;
import groovy.transform.CompileStatic;

/**
 * The filter to trace profiling information.
 */
@CompileStatic
public class ProfilingFilter implements JunboHttpContextScopeListener {
    @Override
    public void begin() {
        if (ProfilingHelper.isProfileEnabled()) {
            ProfilingHelper.beginScope("INPROC", "%s %s", JunboHttpContext.getRequestMethod(), JunboHttpContext.getRequestUri());
        }
    }

    @Override
    public void end() {
        if (ProfilingHelper.isProfileEnabled()) {
            Integer responseStatus = JunboHttpContext.getResponseStatus();
            if (responseStatus == null) {
                responseStatus = 200;
            }
            ProfilingHelper.endScope("(Done) %d", responseStatus);
        }
    }
}
