/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.browse;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.enumid.CountryId;
import com.junbo.common.enumid.LocaleId;
import com.junbo.common.id.TosId;

import javax.ws.rs.QueryParam;

/**
 * The GetTocRequest class.
 */
public class AcceptTosRequest {

    private LocaleId locale;

    private CountryId country;

    @JsonProperty("tos")
    private TosId tosId;

    public LocaleId getLocale() {
        return locale;
    }

    public void setLocale(LocaleId locale) {
        this.locale = locale;
    }

    public CountryId getCountry() {
        return country;
    }

    public void setCountry(CountryId country) {
        this.country = country;
    }

    public TosId getTosId() {
        return tosId;
    }

    public void setTosId(TosId tosId) {
        this.tosId = tosId;
    }
}
