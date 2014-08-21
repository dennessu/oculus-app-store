/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.browse;

import com.junbo.common.enumid.CountryId;
import com.junbo.common.enumid.LocaleId;

import javax.ws.rs.QueryParam;

/**
 * The TocRequest class.
 */
public class TocRequest {

    @QueryParam("locale")
    private LocaleId locale;

    @QueryParam("country")
    private CountryId country;

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
}
