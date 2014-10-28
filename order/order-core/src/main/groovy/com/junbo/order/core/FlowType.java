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
    RATE_ORDER,
    UPDATE_TENTATIVE,
    ASYNC_SETTLE,
    AUTH_SETTLE,
    FREE_SETTLE,
    IMMEDIATE_SETTLE,
    GET_ORDER,
    PREORDER_SETTLE,
    COMPLETE_PREORDER,
    CAPTURE_ORDER,
    UPDATE_NON_TENTATIVE,
    WEB_PAYMENT_CHARGE,
    WEB_PAYMENT_SETTLE,
    CANCEL_ORDER,
    REFUND_ORDER,
    FREE_ORDER
}
