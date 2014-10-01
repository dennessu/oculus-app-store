// CHECKSTYLE:OFF
/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.error;

/**
 * Interface for Error.
 */
public class ErrorDetail {
    public ErrorDetail() {
    }

    public ErrorDetail(String field, String reason) {
        this.field = field;
        this.reason = reason;
    }

    private String field;
    private String reason;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
