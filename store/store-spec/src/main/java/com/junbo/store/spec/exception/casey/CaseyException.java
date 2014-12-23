/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.exception.casey;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * The CaseyException class.
 */
public class CaseyException extends Exception {

    private final String errorCode;

    private final JsonNode details;

    public String getErrorCode() {
        return errorCode;
    }

    public JsonNode getDetails() {
        return details;
    }

    public CaseyException(String errorCode, JsonNode details, String msg, Throwable cause) {
        super(msg, cause);
        this.details = details;
        this.errorCode = errorCode;
    }

    public CaseyException(String errorCode, JsonNode details, String msg) {
        super(msg);
        this.details = details;
        this.errorCode = errorCode;
    }
}
