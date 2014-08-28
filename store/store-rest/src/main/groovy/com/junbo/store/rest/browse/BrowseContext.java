/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.rest.browse;

import com.junbo.identity.spec.v1.model.Country;
import com.junbo.identity.spec.v1.model.Currency;
import com.junbo.identity.spec.v1.model.Locale;
import com.junbo.identity.spec.v1.model.User;
import com.junbo.store.spec.model.Platform;

/**
 * The BrowseContext class.
 */
public class BrowseContext {

    private Platform platform;

    private Locale locale;

    private Country country;

    private Currency currency;

    private User user;

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
