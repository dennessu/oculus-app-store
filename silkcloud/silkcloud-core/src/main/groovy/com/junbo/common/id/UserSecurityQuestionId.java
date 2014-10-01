/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Created by liangfu on 3/13/14.
 */
@IdResourcePath(value = "/users/{userId}/security-questions/{0}",
                resourceType = "security-questions",
                regex = "/users/(?<userId>[0-9A-Za-z]+)/security-questions/(?<id>[0-9A-Za-z]+)")
public class UserSecurityQuestionId extends CloudantId {
    public UserSecurityQuestionId() {}
    public UserSecurityQuestionId(String value) {
        super(value);
    }
}
