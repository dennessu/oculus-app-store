/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.core;

import com.junbo.langur.core.promise.Promise;
import com.junbo.order.core.impl.orderaction.context.OrderActionContext;

/**
 * @param <T> this describes type parameter
 * Created by chriszhu on 2/7/14.
 */
public interface OrderAction<T extends OrderActionContext> {

    Promise<T> execute(T context);
}
