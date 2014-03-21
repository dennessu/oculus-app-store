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

    // api input error
    public static final String INVALID_NULL_EMPTY_INPUT_PARAM = "10000";

    public static final String INVALID_OBJECT_TYPE = "10001";

    public static final String MISSING_PARAMETER_FIELD = "10002";

    public static final String UNNECESSARY_PARAMETER_FIELD = "10003";

    public static final String INVALID_FIELD = "10004";

    public static final String ENUM_CONVERSION_ERROR = "10005";

    // order error starts from 20000
    public static final String ORDER_NOT_FOUND = "20000";

    public static final String ORDER_ITEM_NOT_FOUND = "20001";

    public static final String ORDER_ACTION_NOT_SUPPORTED = "20002";

    public static final String ORDER_TYPE_NOT_SUPPORTED = "20003";

    public static final String ORDER_EVENT_NOT_FOUND = "20004";

}
