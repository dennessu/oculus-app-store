/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.spec.error;

/**
 * Error code of AppErrors.
 */
public class ErrorCode {
    private ErrorCode() {}

    public static final String USER_NOT_FOUND ="USER_NOT_FOUND";

    public static final String USER_STATUS_INVALID = "USER_STATUS_INVALID";

    public static final String FIELD_MISSING_VALUE = "FIELD_MISSING_VALUE";

    public static final String FIELD_INVALID_VALUE = "FIELD_INVALID_VALUE";

    public static final String EMAIL_SEND_ERROR = "EMAIL_SEND_ERROR";

    public static final String PAYLOAD_IS_NULL = "PAYLOAD_IS_NULL";

    public static final String PROPERTIES_INVALID="PROPERTIES_INVALID";

    public static final String TEMPLATE_NOT_FOUND="TEMPLATE_NOT_FOUND";

    public static final String EMAIL_STATUS_INVALID ="EMAIL_STATUS_INVALID";

    public static final String INTERNAL_ERROR ="INTERNAL_ERROR";

}
