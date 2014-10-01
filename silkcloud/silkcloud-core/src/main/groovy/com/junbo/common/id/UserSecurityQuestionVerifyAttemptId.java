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
                resourceType = "security-question-verify-attempts",
                regex = "/users/(?<userId>[0-9A-Za-z]+)/security-question-verify-attempts/(?<id>[0-9A-Za-z]+)")
public class UserSecurityQuestionVerifyAttemptId extends CloudantId {
    public UserSecurityQuestionVerifyAttemptId() {}
    public UserSecurityQuestionVerifyAttemptId(String value) {
        super(value);
    }
}
