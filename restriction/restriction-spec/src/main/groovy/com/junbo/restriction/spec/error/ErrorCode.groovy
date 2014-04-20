/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.restriction.spec.error

/**
 * ErrorCode.
 */
class ErrorCode {
    private ErrorCode() { }

    public static final String USER_NOT_FOUND = 'USER_NOT_FOUND'

    public static final String USER_PII_NOT_FOUND = 'USER_PII_NOT_FOUND'

    public static final String INVALID_USER_STATUS = 'INVALID_USER_STATUS'

    public static final String UNNECESSARY_FILED = 'UNNECESSARY_FILED'

    public static final String INVALID_FIELD_VALUE = 'INVALID_FIELD_VALUE'

    public static final String MISSING_FIELD_VALUE = 'MISSING_FIELD_VALUE'

    public static final String QUERY_PARAMETER_IS_NULL = 'QUERY_PARAMETER_IS_NULL'

    public static final String GET_OFFER_FAILED = 'GET_OFFER_FAILED'

    public static final String GET_USER_FAILED = 'GET_USER_FAILED'

}
