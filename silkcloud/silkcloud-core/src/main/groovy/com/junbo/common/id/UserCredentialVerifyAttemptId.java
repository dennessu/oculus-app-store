/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Created by liangfu on 4/3/14.
 */
@IdResourcePath(value = "/credential-attempts/{0}",
        resourceType = "credential-attempts",
        regex = "/credential-attempts/(?<id>[0-9A-Za-z]+)")
public class UserCredentialVerifyAttemptId extends CloudantId {
    public UserCredentialVerifyAttemptId() {}
    public UserCredentialVerifyAttemptId(String value) {
        super(value);
    }
}
