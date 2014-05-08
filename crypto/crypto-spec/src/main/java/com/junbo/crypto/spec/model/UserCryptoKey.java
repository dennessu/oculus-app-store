/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.crypto.spec.model;

import com.junbo.common.id.UserId;

/**
 * Created by liangfu on 5/7/14.
 */
public class UserCryptoKey {
    private UserId userId;

    // The user encrypted key
    private String cryptoValue;

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public String getCryptoValue() {
        return cryptoValue;
    }

    public void setCryptoValue(String cryptoValue) {
        this.cryptoValue = cryptoValue;
    }
}
