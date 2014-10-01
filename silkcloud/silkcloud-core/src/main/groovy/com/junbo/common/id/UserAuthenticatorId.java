/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Created by liangfu on 3/14/14.
 */
@IdResourcePath(value = "/authenticators/{0}",
                resourceType = "authenticators",
                regex = "/authenticators/(?<id>[0-9A-Za-z]+)")
public class UserAuthenticatorId extends CloudantId {
    public UserAuthenticatorId() {}
    public UserAuthenticatorId(String value) {
        super(value);
    }
}
