/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.login;

/**
 * The UserCredentialChangeRequest class.
 */
public class UserCredentialChangeRequest {

    private String username;
    private UserCredential newCredential;
    private UserCredential oldCredential;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserCredential getNewCredential() {
        return newCredential;
    }

    public void setNewCredential(UserCredential newCredential) {
        this.newCredential = newCredential;
    }

    public UserCredential getOldCredential() {
        return oldCredential;
    }

    public void setOldCredential(UserCredential oldCredential) {
        this.oldCredential = oldCredential;
    }
}
