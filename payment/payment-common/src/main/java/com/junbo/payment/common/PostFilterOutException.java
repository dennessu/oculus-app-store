/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.common;
/**
 * Post FilterOut Exception.
 */
public class PostFilterOutException extends RuntimeException{
    private final String field;
    public PostFilterOutException(String field){
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
