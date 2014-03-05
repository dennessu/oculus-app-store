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

    public static final String USER_NOT_FOUND = "USER_NOT_FOUND";

    public static final String USER_STATUS_INVALID = "USER_STATUS_INVALID";

    public static final String PAYMENT_STATUS_INVALID = "PAYMENT_STATUS_INVALID";

    public static final String ORDER_NOT_FOUND = "ORDER_NOT_FOUND";

    public static final String ORDER_ITEM_NOT_FOUND = "ORDER_ITEM_NOT_FOUND";

    public static final String INVALID_FIELD = "INVALID_FIELD";

}
