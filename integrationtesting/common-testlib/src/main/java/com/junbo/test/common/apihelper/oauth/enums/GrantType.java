/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.oauth.enums;

/**
 * Created by weiyu_000 on 7/9/14.
 */
public enum GrantType {
    AUTHORIZATION_CODE("authorization_code"),
    REFRESH_TOKEN("refresh_token"),
    CLIENT_CREDENTIALS("client_credentials"),
    PASSWORD("password");

    String type;

    GrantType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }

}
