/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.error;

/**
 * Created by chriszhu on 3/20/14.
 */
public class BillingErrorCode {

    private BillingErrorCode() {}

    // billing error starts from 33000
    public static final String BALANCE_NOT_FOUND = "33000";

    public static final String BILLING_CONNECTION_ERROR = "33001";
}
