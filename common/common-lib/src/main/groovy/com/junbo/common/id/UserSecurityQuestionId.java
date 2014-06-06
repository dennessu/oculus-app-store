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
        regex = "/users/(?<userId>[0-9A-Z]+)/security-questions/(?<id>[0-9A-Z]+)")
public class UserSecurityQuestionId extends Id {
    public UserSecurityQuestionId() {}
    public UserSecurityQuestionId(long value) {
        super(value);
    }
}
