/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.billing;

import com.junbo.common.enumid.CountryId;
import com.junbo.common.enumid.LocaleId;

/**
 * The BillingUpdateRequest class.
 */
public class InstrumentUpdateRequest {

    private LocaleId locale;

    private CountryId country;

    private Instrument instrument;

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

    public Instrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }
}
