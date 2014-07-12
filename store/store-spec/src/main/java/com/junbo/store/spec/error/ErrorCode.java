/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.store.spec.error;

/**
 * ErrorCode for iap.
 */
public class ErrorCode {

    private ErrorCode() {
    }

    public static final String STORE_VALUE_PI_NOT_FOUND = "iap:10000";

    public static final String RESOURCE_NOT_FOUND = "iap:10001";

    public static final String ENTITLEMENT_NOT_CONSUMABLE = "iap:10002";

    public static final String ENTITLEMENT_NOT_ENOUGH_USECOUNT = "iap:10003";

    public static final String ITEM_NOT_FOUND_WITH_PACKAGE_NAME = "iap:10004";
}
