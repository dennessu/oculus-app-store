/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.purchase;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.enumid.CountryId;
import com.junbo.common.enumid.LocaleId;
import com.junbo.common.id.OfferId;
import com.junbo.common.id.UserId;

/**
 * The MakeFreePurchaseRequest class.
 */
public class MakeFreePurchaseRequest {

    @JsonProperty("user")
    private UserId userId;

    @JsonProperty("offer")
    private OfferId offerId;

    private CountryId country;

    private LocaleId locale;

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public OfferId getOfferId() {
        return offerId;
    }

    public void setOfferId(OfferId offerId) {
        this.offerId = offerId;
    }

    public CountryId getCountry() {
        return country;
    }

    public void setCountry(CountryId country) {
        this.country = country;
    }

    public LocaleId getLocale() {
        return locale;
    }

    public void setLocale(LocaleId locale) {
        this.locale = locale;
    }
}
