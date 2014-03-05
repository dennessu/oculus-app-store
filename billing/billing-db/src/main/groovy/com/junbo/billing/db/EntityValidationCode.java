/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db;

/**
 * Created by xmchen on 14-1-17.
 */
public class EntityValidationCode {
    private EntityValidationCode() {
    }

    public static final String INVALID_VALUE = "INVALID_VALUE";
    public static final String MISSING_VALUE = "MISSING_VALUE";
    public static final String TOO_LONG = "TOO_LONG";
    public static final String TOO_SHORT = "TOO_SHORT";
    public static final String TOO_LOW = "TOO_LOW";
    public static final String TOO_HIGH = "TOO_HIGH";
}
