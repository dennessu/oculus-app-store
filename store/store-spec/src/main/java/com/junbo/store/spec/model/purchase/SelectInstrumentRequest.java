/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.purchase;

import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.common.id.UserId;

/**
 * The SelectInstrumentRequest class.
 */
public class SelectInstrumentRequest {
    private String purchaseToken;
    private UserId userId;
    private PaymentInstrumentId instrumentId;

    public String getPurchaseToken() {
        return purchaseToken;
    }

    public void setPurchaseToken(String purchaseToken) {
        this.purchaseToken = purchaseToken;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public PaymentInstrumentId getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(PaymentInstrumentId instrumentId) {
        this.instrumentId = instrumentId;
    }
}
