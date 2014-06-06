/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Created by liangfu on 3/14/14.
 */
@IdResourcePath(value = "/authenticators/{0}", regex = "/authenticators/(?<id>[0-9A-Z]+)")
public class UserAuthenticatorId extends Id {
    public UserAuthenticatorId() {}
    public UserAuthenticatorId(long value) {
        super(value);
    }
}
