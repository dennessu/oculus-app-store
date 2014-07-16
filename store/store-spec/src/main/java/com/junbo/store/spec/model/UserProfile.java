/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model;

import com.junbo.common.id.UserId;

/**
 * The UserProfile class.
 */
public class UserProfile {

    private UserId userId;
    private String username;
    private String email;
    private Boolean tfaEnabled;

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
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
