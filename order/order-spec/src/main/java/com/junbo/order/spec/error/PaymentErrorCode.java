/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.error;

/**
 * Created by chriszhu on 3/20/14.
 */
public class PaymentErrorCode {

    private PaymentErrorCode() {}

    // payment error code starts from 31000

    public static final String PAYMENT_INSTRUMENT_STATUS_INVALID = "21000";

    public static final String PAYMENT_INSTRUMENT_NOT_FOUND = "21001";

    public static final String PAYMENT_CONNECTION_ERROR = "21002";
}
