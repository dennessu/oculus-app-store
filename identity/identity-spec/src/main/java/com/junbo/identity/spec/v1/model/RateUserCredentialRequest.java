/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

/**
 * Created by kg on 4/3/14.
 */
public class RateUserCredentialRequest {

    private String type;

    private String value;

    private RateUserCredentialContext context;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public RateUserCredentialContext getContext() {
        return context;
    }

    public void setContext(RateUserCredentialContext context) {
        this.context = context;
    }
}
