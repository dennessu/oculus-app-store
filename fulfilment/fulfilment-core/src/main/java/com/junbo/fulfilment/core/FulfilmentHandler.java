/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.core;

import com.junbo.fulfilment.core.context.FulfilmentContext;

/**
 * FulfilmentHandler.
 *
 * @param <T> fulfilment context type
 */
public interface FulfilmentHandler<T extends FulfilmentContext> {
    void process(T context);

    void revoke(T context);
}
