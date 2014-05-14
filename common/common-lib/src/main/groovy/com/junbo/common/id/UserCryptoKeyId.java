/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Created by liangfu on 5/12/14.
 */
@IdResourcePath("/crypto/{0}")
public class UserCryptoKeyId extends Id {
    public UserCryptoKeyId() {}
    public UserCryptoKeyId(long value) {
        super(value);
    }
}
