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

    public static final String BILLING_CHARGE_FAILED = "33002";

    public static final String BILLING_CONFIRM_BALANCE_FAILED = "33003";

    public static final String BILLING_INSUFFICIENT_FUND = "33004";

    public static final String BILLING_REFUND_FAILED = "33005";

    public static final String BILLING_TAX_FAILED = "33006";

    public static final String BILLING_RESULT_INVALID = "33007";

    public static final String BILLING_AUDIT_FAILED = "33008";
}
