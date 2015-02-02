/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.jobs.transaction.model;

/**
 * Created by acer on 2015/1/30.
 */
public enum DiscrepancyReason {
    CHARGE_MISMATCH,
    REFUND_MISMATCH,
    CHARGE_BACK,
    CHARGE_BACK_REVERSE,
    CHARGE_BACK_OTW,
    CHARGE_BACK_REVERSE_OTW,
    DECLINE
}
