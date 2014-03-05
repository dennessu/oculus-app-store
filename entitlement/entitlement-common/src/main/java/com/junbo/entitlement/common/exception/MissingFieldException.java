/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.common.exception;

/**
 * Exception subClass.
 */
public class MissingFieldException extends EntitlementException {
    private final String fieldName;

    public MissingFieldException(String fieldName) {
        super();
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
