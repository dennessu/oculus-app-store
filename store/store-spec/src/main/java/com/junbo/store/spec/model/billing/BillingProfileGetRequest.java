/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.billing;

import com.junbo.common.id.UserId;

import javax.ws.rs.QueryParam;

/**
 * The BillingProfileGetRequest class.
 */
public class BillingProfileGetRequest {

    @QueryParam("locale")
    private String locale;

    @QueryParam("username")
    private String username;

    @QueryParam("userId")
    private UserId userId;

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }
}
