/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model;


/**
 * The CommonHeaders class.
 */
public enum  CommonHeaders {

    ANDROID_ID("X-ANDROID-ID"),

    ACCEPT_LANGUAGE("Accept-Language"),

    USER_AGENT("User-Agent"),

    MCCMNC("X-MCCMNC");

    private final String value;

    private CommonHeaders(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
