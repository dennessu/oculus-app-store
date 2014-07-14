/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model;

/**
 * The UserProfile class.
 */
public class UserProfile {

    private String userId;
    private String username;
    private String email;
    private Boolean tfaEnabled;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getTfaEnabled() {
        return tfaEnabled;
    }

    public void setTfaEnabled(Boolean tfaEnabled) {
        this.tfaEnabled = tfaEnabled;
    }
}
