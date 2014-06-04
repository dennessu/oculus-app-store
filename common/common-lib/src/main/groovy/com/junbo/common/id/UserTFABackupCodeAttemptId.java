/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Created by liangfu on 4/22/14.
 */
@IdResourcePath(value = "/users/{userId}/tfa-backup-attempts/{0}",
        regex = "/users/(?<userId>[0-9A-Z]+)/tfa-backup-attempts/(?<id>[0-9A-Z]+)")
public class UserTFABackupCodeAttemptId extends Id {

    public UserTFABackupCodeAttemptId() {}
    public UserTFABackupCodeAttemptId(long value) {
        super(value);
    }
}
