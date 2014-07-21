/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.spec.internal;

import com.junbo.common.model.ResourceMetaForDualWrite;

/**
 * Created by Administrator on 14-7-3.
 */
public class TokenSetOffer extends ResourceMetaForDualWrite<String> {
    private String id;
    private String tokenSetId;
    private String productId;
    private String productType;
    private Boolean isDefault;

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTokenSetId() {
        return tokenSetId;
    }

    public void setTokenSetId(String tokenSetId) {
        this.tokenSetId = tokenSetId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

}
