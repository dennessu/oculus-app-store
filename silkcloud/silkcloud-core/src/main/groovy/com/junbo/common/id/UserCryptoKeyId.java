/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Created by liangfu on 5/12/14.
 */
@IdResourcePath(value = "/crypto/{0}",
                resourceType = "crypto",
                regex = "/crypto/(?<id>[0-9A-Za-z]+)")
public class UserCryptoKeyId extends CloudantId {
    public UserCryptoKeyId() {}
    public UserCryptoKeyId(String value) {
        super(value);
    }
}
