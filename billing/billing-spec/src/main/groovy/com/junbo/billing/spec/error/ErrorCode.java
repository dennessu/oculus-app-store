/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.spec.error;

/**
 * Created by xmchen on 14-2-25.
 */
public class ErrorCode {
    private ErrorCode() {
    }

    public static final String USER_NOT_FOUND = "USER_NOT_FOUND";

    public static final String USER_STATUS_INVALID = "USER_STATUS_INVALID";

    public static final String SHIPPING_ADDRESS_NOT_FOUND = "SHIPPING_ADDRESS_NOT_FOUND";

    public static final String USER_SHIPPING_ADDRESS_NOT_MATCH = "USER_SHIPPING_ADDRESS_NOT_MATCH";

    public static final String FIELD_MISSING_VALUE = "FIELD_MISSING_VALUE";

    public static final String CURRENCY_NOT_FOUND = "CURRENCY_NOT_FOUND";

    public static final String PAYMENT_INSTRUMENT_NOT_FOUND = "PAYMENT_INSTRUMENT_NOT_FOUND";

    public static final String INVALID_BALANCE_TYPE = "INVALID_BALANCE_TYPE";

    public static final String INVALID_BALANCE_TOTAL = "INVALID_BALANCE_TOTAL";

    public static final String TAX_CALCULATION_ERROR = "TAX_CALCULATION_ERROR";
}
