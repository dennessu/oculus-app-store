/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.core;

/**
 * Created by fzhang on 14-3-5.
 */
public enum FlowType {
    RATE,
    ASYNC_SETTLE,
    AUTH_SETTLE,
    FREE_SETTLE,
    IMMEDIATE_SETTLE,
    GET_ORDER
}
