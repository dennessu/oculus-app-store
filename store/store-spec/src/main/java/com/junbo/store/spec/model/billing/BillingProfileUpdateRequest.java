/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.billing;

import com.junbo.common.enumid.LocaleId;
import com.junbo.common.id.UserId;

/**
 * The BillingProfileUpdateRequest class.
 */
public class BillingProfileUpdateRequest {


    /**
     * The Operation enum.
     */
    public enum UpdateAction {
        ADD_PI,
        UPDATE_PI,
        REMOVE_PI
    }

    private UserId userId;

    private LocaleId locale;

    private String action;

    private Instrument instrument;

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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }
}
