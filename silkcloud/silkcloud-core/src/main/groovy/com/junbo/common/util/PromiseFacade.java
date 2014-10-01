/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.util;

import com.junbo.langur.core.promise.Promise;
import com.junbo.langur.core.promise.PromiseShell;

import java.util.concurrent.Callable;

/**
 * PromiseFacade.
 */
public enum PromiseFacade {
    DEFAULT,
    PAYMENT,
    BILLING,
    CATALOG,
    SUBSCRIPTION,
    ORDER,
    IDENTITY,
    RATING,
    CART,
    FULFILMENT;

    public <R> Promise<R> decorate(Callable<R> callable) {
        return PromiseShell.decorate(name(), callable);
    }

    public Promise<Void> decorate(Runnable runnable) {
        return PromiseShell.decorate(name(), runnable);
    }
}
