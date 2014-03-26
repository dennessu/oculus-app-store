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

    public static final String INVALID_EMAIL_ID ="INVALID_EMAIL_ID";

    public static final String USER_NOT_FOUND ="USER_NOT_FOUND";

    public static final String INVALID_USER_STATUS = "INVALID_USER_STATUS";

    public static final String MISSING_FIELD_VALUE = "MISSING_FIELD_VALUE";

    public static final String INVALID_FIELD_VALUE = "INVALID_FIELD_VALUE";

    public static final String UNNECESSARY_FILED = "UNNECESSARY_FILED";

    public static final String INVALID_PARAMETER = "INVALID_PARAMETER";

    public static final String PAYLOAD_IS_NULL = "PAYLOAD_IS_NULL";

    public static final String INVALID_PROPERTIES = "INVALID_PROPERTIES";

    public static final String EMAIL_TEMPLATE_NOT_FOUND = "EMAIL_TEMPLATE_NOT_FOUND";

    public static final String TEMPLATE_NAME_ALREADY_EXIST = "TEMPLATE_NAME_ALREADY_EXIST";

    public static final String EMAIL_SCHEDULE_NOT_FOUND ="EMAIL_SCHEDULE_NOT_FOUND";

    public static final String EMAIL_STATUS_INVALID ="EMAIL_STATUS_INVALID";

    public static final String EMAIL_SEND_ERROR = "EMAIL_SEND_ERROR";

    public static final String INTERNAL_ERROR ="INTERNAL_ERROR";

}
