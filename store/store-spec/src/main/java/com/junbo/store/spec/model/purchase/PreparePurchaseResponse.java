/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.purchase;

import com.junbo.store.spec.model.BaseResponse;

/**
 * The PreparePurchaseResponse class.
 */
public class PreparePurchaseResponse extends BaseResponse {

    private String formattedTotalPrice;
    private String purchaseToken;

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
}
