/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.filter;

import com.junbo.common.util.Context;
import com.junbo.langur.core.filter.AfterInvocationFilter;
import com.junbo.langur.core.promise.Promise;

/**
 * Response filter.
 */
public class RestAdapterAfterInvocationFilter implements AfterInvocationFilter {

    @Override
    public Promise<Void> process() {
        return Context.get().executeCleanupActions().then(new Promise.Func<Void, Promise<Void>>() {
            @Override
            public Promise<Void> apply(Void aVoid) {
                return Context.get().drainPendingTasks();
            }
        });
    }
}
