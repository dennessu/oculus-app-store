/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Created by liangfu on 4/22/14.
 */
@IdResourcePath("/users/{userId}/tele-attempts/{0}")
public class UserTeleAttemptId extends Id {

    public UserTeleAttemptId() {}
    public UserTeleAttemptId(long value) {
        super(value);
    }
}
