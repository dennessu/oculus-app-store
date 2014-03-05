/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.common.exception;

/**
 * Exception subClass.
 */
public class NotTransferableException extends EntitlementException {
    private final String id;
    private final String reason;

    public NotTransferableException(Long id, String reason) {
        super();
        this.id = id.toString();
        this.reason = reason;
    }

    public String getId() {
        return id;
    }

    public String getReason() {
        return reason;
    }
}
