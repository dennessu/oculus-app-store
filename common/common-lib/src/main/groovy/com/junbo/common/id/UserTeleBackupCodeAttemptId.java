/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Created by liangfu on 4/22/14.
 */
@IdResourcePath(value = "/users/{userId}/tele-backup-attempts/{0}",
        regex = "/users/(?<userId>[0-9A-Z]+)/tele-backup-attempts/(?<id>[0-9A-Z]+)")
public class UserTeleBackupCodeAttemptId extends Id {

    public UserTeleBackupCodeAttemptId() {}
    public UserTeleBackupCodeAttemptId(long value) {
        super(value);
    }
}
