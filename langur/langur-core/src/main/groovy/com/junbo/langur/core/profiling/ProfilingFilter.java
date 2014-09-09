/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.profiling;

import com.junbo.langur.core.context.JunboHttpContext;
import com.junbo.langur.core.context.JunboHttpContextScopeListener;
import groovy.transform.CompileStatic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The filter to trace profiling information.
 */
@CompileStatic
public class ProfilingFilter implements JunboHttpContextScopeListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfilingFilter.class);

    @Override
    public void begin() {
        ProfilingHelper.beginScope(LOGGER, "(INPROC) BEGIN %s %s ", JunboHttpContext.getRequestMethod(), JunboHttpContext.getRequestUri());
    }

    @Override
    public void end() {
        ProfilingHelper.endScope(LOGGER, "(INPROC) END %s %s %d", JunboHttpContext.getRequestMethod(), JunboHttpContext.getRequestUri(), JunboHttpContext.getResponseStatus());
    }
}
