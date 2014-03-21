/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.error;

/**
 * Created by chriszhu on 3/21/14.
 */
public class RatingErrorCode {
    private RatingErrorCode() {}

    // rating error code starts from 36000

    public static final String RATING_CONNECTION_ERROR = "36000";

    public static final String RATING_RESULT_INVALID = "36001";

}
