/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.common;

/**
 * Pre Validation Exception.
 */
public class PreValidationException extends RuntimeException{
    private final String field;
    public PreValidationException(String field){
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
