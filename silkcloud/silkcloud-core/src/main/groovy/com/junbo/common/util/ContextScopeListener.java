/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.util;

import com.junbo.langur.core.context.JunboHttpContext;
import com.junbo.langur.core.context.JunboHttpContextScopeListener;

/**
 * ContextScopeListener.
 */
public class ContextScopeListener implements JunboHttpContextScopeListener {

    @Override
    public void begin() {
        JunboHttpContext.getProperties().put("routing-context", Context.get());
        Context.clear();
    }

    @Override
    public void end() {
        Context.set((Context.Data)JunboHttpContext.getProperties().get("routing-context"));
    }
}
