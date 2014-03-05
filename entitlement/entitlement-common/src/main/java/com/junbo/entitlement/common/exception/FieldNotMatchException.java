/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.common.exception;

/**
 * Exception subClass.
 */
public class FieldNotMatchException extends EntitlementException {
    private final String fieldName;
    private final String actual;
    private final String expected;

    public FieldNotMatchException(String fieldName, String actual, String expected) {
        super();
        this.fieldName = fieldName;
        this.actual = actual;
        this.expected = expected;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getActual() {
        return actual;
    }

    public String getExpected() {
        return expected;
    }
}
