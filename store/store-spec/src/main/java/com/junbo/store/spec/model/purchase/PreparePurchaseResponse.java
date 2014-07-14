/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.purchase;

/**
 * The PreparePurchaseResponse class.
 */
public class PreparePurchaseResponse {

    private String formattedOriginalPrice;
    private String formattedFinalPrice;
    private String purchaseToken;

    public String getFormattedOriginalPrice() {
        return formattedOriginalPrice;
    }

    public void setFormattedOriginalPrice(String formattedOriginalPrice) {
        this.formattedOriginalPrice = formattedOriginalPrice;
    }

    public String getFormattedFinalPrice() {
        return formattedFinalPrice;
    }

    public void setFormattedFinalPrice(String formattedFinalPrice) {
        this.formattedFinalPrice = formattedFinalPrice;
    }

    public String getPurchaseToken() {
        return purchaseToken;
    }

    public void setPurchaseToken(String purchaseToken) {
        this.purchaseToken = purchaseToken;
    }
}
