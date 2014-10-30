/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.external.sentry;

/**
 * Created by liangfu on 10/23/14.
 */
public enum SentryFieldConstant {
    NAME("oculus_registration_create"),
    ANDRIOD_ID("AndroidID"),
    EMAIL("email"),
    USERNAME("username"),
    REAL_NAME("realname"),
    APP_JSON("AppJson");

    private final String value;

    private SentryFieldConstant(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
