/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.purchase;

import com.junbo.store.spec.model.Challenge;
import com.junbo.store.spec.model.billing.Instrument;

/**
 * The PreparePurchaseResponse class.
 */
public class PreparePurchaseResponse {

    private String formattedTotalPrice;

    private Instrument instrument;

    private String purchaseToken;

    private Challenge challenge;

    public Instrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }

    public String getFormattedTotalPrice() {
        return formattedTotalPrice;
    }

    public void setFormattedTotalPrice(String formattedTotalPrice) {
        this.formattedTotalPrice = formattedTotalPrice;
    }

    public String getPurchaseToken() {
        return purchaseToken;
    }

    public void setPurchaseToken(String purchaseToken) {
        this.purchaseToken = purchaseToken;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }
}
