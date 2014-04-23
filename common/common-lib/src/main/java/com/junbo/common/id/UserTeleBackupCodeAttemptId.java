/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Created by liangfu on 4/22/14.
 */
@IdResourcePath("/users/{userId}/tele-backup-attempts/{0}")
public class UserTeleBackupCodeAttemptId extends Id {

    public UserTeleBackupCodeAttemptId() {}
    public UserTeleBackupCodeAttemptId(long value) {
        super(value);
    }
}
