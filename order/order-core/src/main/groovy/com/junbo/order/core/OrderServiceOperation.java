/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.core;

/**
 * Created by chriszhu on 2/11/14.
 */
public enum OrderServiceOperation {
    CREATE,
    GET,
    CREATE_TENTATIVE,
    SETTLE_TENTATIVE,
    UPDATE,
    CANCEL,
    ADJUST
}
