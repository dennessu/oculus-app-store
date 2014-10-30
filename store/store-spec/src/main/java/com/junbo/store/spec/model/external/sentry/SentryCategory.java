/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.external.sentry;

/**
 * Created by liangfu on 10/23/14.
 */
public enum SentryCategory {
    OCULUS_REGISTRATION_CREATE("oculus_registration_create"),

    OCULUS_LOGIN_MOBILE("oculus_login_mobile"),

    OCULUS_LOGIN_WEB("oculus_login_web");

    private final String value;

    private SentryCategory(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
