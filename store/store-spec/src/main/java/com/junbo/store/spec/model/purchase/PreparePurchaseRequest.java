/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.purchase;

import com.junbo.common.id.OfferId;
import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.store.spec.model.ChallengeAnswer;

/**
 * The PreparePurchaseRequest class.
 */
public class PreparePurchaseRequest {

    private PaymentInstrumentId instrument;

    private String purchaseToken;

    private OfferId offer;

    private Boolean isIAP;

    private ChallengeAnswer challengeAnswer;

    public PaymentInstrumentId getInstrument() {
        return instrument;
    }

    public void setInstrument(PaymentInstrumentId instrument) {
        this.instrument = instrument;
    }

    public OfferId getOffer() {
        return offer;
    }

    public void setOffer(OfferId offer) {
        this.offer = offer;
    }

    public String getPurchaseToken() {
        return purchaseToken;
    }

    public void setPurchaseToken(String purchaseToken) {
        this.purchaseToken = purchaseToken;
    }

    public ChallengeAnswer getChallengeAnswer() {
        return challengeAnswer;
    }

    public void setChallengeAnswer(ChallengeAnswer challengeAnswer) {
        this.challengeAnswer = challengeAnswer;
    }

    public Boolean getIsIAP() {
        return isIAP;
    }

    public void setIsIAP(Boolean isIAP) {
        this.isIAP = isIAP;
    }
}
