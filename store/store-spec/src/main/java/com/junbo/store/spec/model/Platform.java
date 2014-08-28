/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model;

/**
 * The Platform class.
 */
public enum Platform {

    ANDROID("ANDROID");

    private Platform(String value) {
        this.value = value;
    }

    private String value;

    public String getValue() {
        return value;
    }

}
