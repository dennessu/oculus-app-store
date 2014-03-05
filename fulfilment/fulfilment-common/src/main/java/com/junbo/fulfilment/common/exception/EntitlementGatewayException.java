/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.common.exception;

/**
 * EntitlementGatewayException.
 */
public class EntitlementGatewayException extends FulfilmentException {
    public EntitlementGatewayException(String message) {
        super(message);
    }

    public EntitlementGatewayException(String message, Exception ex) {
        super(message, ex);
    }
}
