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
    BILLING_HISTORY_NOT_FOUND,
    BILLING_HISTORY_AMOUNT_NOT_MATCH,
    CURRENCY_NOT_MATCH
}
