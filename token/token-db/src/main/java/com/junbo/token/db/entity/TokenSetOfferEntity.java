/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.db.entity;

import com.junbo.token.spec.enums.ProductType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * token set offer entity.
 */
@Entity
@Table(name = "token_set_offer")
public class TokenSetOfferEntity extends GenericEntity {

    @Id
    @Column(name = "set_offer_id")
    private String id;

    @Column(name = "set_id")
    private String tokenSetId;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "product_type")
    private ProductType productType;

    @Column(name = "is_default")
    private Boolean isDefault;

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

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }
}
