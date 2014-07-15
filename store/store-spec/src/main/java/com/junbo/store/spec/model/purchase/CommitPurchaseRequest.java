/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.purchase;

import com.junbo.store.spec.model.ChallengeSolution;

/**
 * The CommitPurchaseRequest class.
 */
public class CommitPurchaseRequest {

    private ChallengeSolution challengeSolution;

    private String purchaseToken;

    private String instrumentId;

    private String packageName;

    private String isIAP;

    public ChallengeSolution getChallengeSolution() {
        return challengeSolution;
    }

    public void setChallengeSolution(ChallengeSolution challengeSolution) {
        this.challengeSolution = challengeSolution;
    }

    public String getPurchaseToken() {
        return purchaseToken;
    }

    public void setPurchaseToken(String purchaseToken) {
        this.purchaseToken = purchaseToken;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getIsIAP() {
        return isIAP;
    }

    public void setIsIAP(String isIAP) {
        this.isIAP = isIAP;
    }
}
