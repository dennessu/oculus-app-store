/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.common.exception;

/**
 * A base class for entitlementException in service layer.
 */
public class EntitlementException extends RuntimeException {
    public EntitlementException() {
    }

    public EntitlementException(String message) {
        super(message);
    }
}
