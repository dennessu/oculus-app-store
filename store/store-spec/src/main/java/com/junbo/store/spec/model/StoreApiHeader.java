/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model;


/**
 * The StoreApiHeader class.
 */
public enum StoreApiHeader {

    ANDROID_ID("X-ANDROID-ID"),

    DEVICE_ID("X-DEVICE-ID"),

    ACCEPT_LANGUAGE("Accept-Language"),

    USER_AGENT("User-Agent"),

    PACKAGE_NAME("X-PACKAGE-NAME"),

    PACKAGE_VERSION("X-PACKAGE-VERSION"),

    PACKAGE_SIGNATURE_HASH("X-PACKAGE-SIGNATURE-HASH"),

    IP_COUNTRY("oculus-geoip-country-code");

    private final String value;

    private StoreApiHeader(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
