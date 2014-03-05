/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.common.exception;

/**
 * BillingGatewayException.
 */
public class BillingGatewayException extends FulfilmentException {
    public BillingGatewayException(String message) {
        super(message);
    }

    public BillingGatewayException(String message, Exception ex) {
        super(message, ex);
    }
}
