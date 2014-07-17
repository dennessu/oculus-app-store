/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.billing;

import com.junbo.common.enumid.LocaleId;
import com.junbo.common.id.UserId;

import javax.ws.rs.QueryParam;

/**
 * The BillingProfileGetRequest class.
 */
public class BillingProfileGetRequest {

    @QueryParam("locale")
    private LocaleId locale;

    @QueryParam("userId")
    private UserId userId;

    public LocaleId getLocale() {
        return locale;
    }

    public void setLocale(LocaleId locale) {
        this.locale = locale;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }
}
