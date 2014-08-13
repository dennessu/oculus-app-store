/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.login;

/**
 * The UserCredentialRateRequest class.
 */
public class UserCredentialRateRequest {

    private UserCredential userCredential;

    private UserCredentialRateContext context;

    public UserCredential getUserCredential() {
        return userCredential;
    }

    public void setUserCredential(UserCredential userCredential) {
        this.userCredential = userCredential;
    }

    public UserCredentialRateContext getContext() {
        return context;
    }

    public void setContext(UserCredentialRateContext context) {
        this.context = context;
    }
}
