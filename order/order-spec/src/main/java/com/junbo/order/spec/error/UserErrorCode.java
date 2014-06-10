/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.error;

/**
 * Created by chriszhu on 3/20/14.
 */
public class UserErrorCode {

    private UserErrorCode() {}

    // user error codes starts from 30000

    public static final String USER_NOT_FOUND = "30000";

    public static final String USER_STATUS_INVALID = "30001";

    public static final String USER_CONNECTION_ERROR = "30002";

    public static final String CURRENCY_NOT_VALID = "30003";
}
