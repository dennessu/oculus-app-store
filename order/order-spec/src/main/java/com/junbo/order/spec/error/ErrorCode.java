/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.error;

/**
 * Created by fzhang@wan-san.com on 14-2-19.
 */
public class ErrorCode {

    private ErrorCode() {
    }

    public static final String INVALID_NULL_EMPTY_INPUT_PARAM = "10000";

    public static final String INVALID_OBJECT_TYPE = "10001";

    public static final String MISSING_PARAMETER_FIELD = "10002";

    public static final String UNNECESSARY_PARAMETER_FIELD = "10003";

    public static final String USER_NOT_FOUND = "10004";

    public static final String USER_STATUS_INVALID = "10005";

    public static final String PAYMENT_STATUS_INVALID = "10006";

    public static final String ORDER_NOT_FOUND = "10007";

    public static final String ORDER_ITEM_NOT_FOUND = "10008";

    public static final String INVALID_FIELD = "10009";

    public static final String ENUM_CONVERSION_ERROR = "10010";

    public static final String ORDER_EVENT_NOT_FOUND = "10011";
}
