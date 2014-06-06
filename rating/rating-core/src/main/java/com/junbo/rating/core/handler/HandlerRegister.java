/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.handler;

import org.springframework.util.Assert;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lizwu on 2/21/14.
 */
public final class HandlerRegister {
    private static ConcurrentHashMap<String, CriterionHandler> register;

    public void setRegister(ConcurrentHashMap<String, CriterionHandler> register) {
        this.register = register;
    }

    public static CriterionHandler getHandler(String predicate) {
        Assert.notNull(predicate, "predicate");

        CriterionHandler handler = register.get(predicate);

        if (handler == null) {
            throw new IllegalStateException(predicate + " handler is not registered.");
        }

        return handler;
    }
}
