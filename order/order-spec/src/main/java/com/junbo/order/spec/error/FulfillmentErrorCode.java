/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.error;

/**
 * Created by chriszhu on 3/20/14.
 */
public class FulfillmentErrorCode {

    private FulfillmentErrorCode() {}

    // fulfillment error stats from 32000
    public static final String FULFILLMENT_NOT_FOUND = "32000";

    public static final String FULFILLMENT_CONNECTION_ERROR = "32001";
}
