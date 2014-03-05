/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model;

/**
 * Created by LinYi on 2/10/14.
 */
public class SellerInfo {
    private Long sellerProfilerId;
    private Long sellerTaxProfileId;

    public Long getSellerProfilerId() {
        return sellerProfilerId;
    }

    public void setSellerProfilerId(Long sellerProfilerId) {
        this.sellerProfilerId = sellerProfilerId;
    }

    public Long getSellerTaxProfileId() {
        return sellerTaxProfileId;
    }

    public void setSellerTaxProfileId(Long sellerTaxProfileId) {
        this.sellerTaxProfileId = sellerTaxProfileId;
    }
}
