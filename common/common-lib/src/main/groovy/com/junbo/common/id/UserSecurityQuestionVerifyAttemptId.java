/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Created by liangfu on 3/13/14.
 */
@IdResourcePath(value = "/users/{userId}/security-question-verify-attempts/{0}",
        regex = "/users/(?<userId>[0-9A-Z]+)/security-question-verify-attempts/(?<id>[0-9A-Z]+)")
public class UserSecurityQuestionVerifyAttemptId extends Id {
    public UserSecurityQuestionVerifyAttemptId() {}
    public UserSecurityQuestionVerifyAttemptId(long value) {
        super(value);
    }
}
