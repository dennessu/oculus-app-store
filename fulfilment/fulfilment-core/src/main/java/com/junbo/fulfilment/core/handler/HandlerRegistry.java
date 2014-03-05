/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.core.handler;

import com.junbo.fulfilment.core.FulfilmentHandler;

import java.util.concurrent.ConcurrentHashMap;

/**
 * HandlerRegistry.
 */
public final class HandlerRegistry {
    private static ConcurrentHashMap<String, FulfilmentHandler> registry;

    public void setRegistry(ConcurrentHashMap<String, FulfilmentHandler> registry) {
        this.registry = registry;
    }

    public static FulfilmentHandler resolve(String actionType) {
        FulfilmentHandler handler = registry.get(actionType);

        if (handler == null) {
            throw new IllegalStateException("[ " + actionType + "] handler is not registered.");
        }

        return handler;
    }
}
