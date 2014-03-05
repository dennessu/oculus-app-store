/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.common.exception;

/**
 * Exception subClass.
 */
public class FieldNotCorrectException extends EntitlementException {
    private final String fieldName;
    private final String msg;

    public FieldNotCorrectException(String fieldName, String msg) {
        super();
        this.fieldName = fieldName;
        this.msg = msg;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getMsg() {
        return msg;
    }
}
