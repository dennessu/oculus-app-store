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

    public static final String UNEXPECTED_ERROR = "10006";

    public static final String INVALID_SETTLED_ORDER_UPDATE = "10007";

    // order error starts from 20000
    public static final String ORDER_NOT_FOUND = "20000";

    public static final String ORDER_ITEM_NOT_FOUND = "20001";

    public static final String ORDER_ACTION_NOT_SUPPORTED = "20002";

    public static final String ORDER_TYPE_NOT_SUPPORTED = "20003";

    public static final String ORDER_EVENT_NOT_FOUND = "20004";

    public static final String ORDER_NOT_TENTATIVE = "20005";

    public static final String EVENT_NOT_SUPPORTED = "20006";

    public static final String ORDER_CAN_NOT_BE_CANCELED = "20007";

    public static final String ORDER_PRICE_CHANGED = "20008";

    public static final String ORDER_CONCURRENT_UPDATE = "20009";

    public static final String ORDER_IS_REFUNDED = "20010";

    public static final String ORDER_ITEM_IS_NOT_FOUND_FOR_REFUNDED = "20010";

    public static final String ORDER_CAN_NOT_BE_REFUNDED = "20011";

    public static final String ORDER_NO_ITEM_TO_REFUND_IN_REQUEST = "20012";

    public static final String ORDER_RISK_REVIEW_ERROR = "20020";

    // subledger error starts from 20000
    public static final String SUBLEDGER_NOT_FOUND = "20200";

    public static final String SUBLEDGER_CONCURRENT_UPDATE = "20201";


}
