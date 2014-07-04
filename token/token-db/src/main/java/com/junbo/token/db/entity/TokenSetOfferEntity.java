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
    private Long id;

    @Column(name = "set_id")
    private Long tokenSetId;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "product_type")
    private ProductType productType;

    @Column(name = "is_default")
    private boolean isDefault;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getShardMasterId() {
        return null;
    }

    public Long getTokenSetId() {
        return tokenSetId;
    }

    public void setTokenSetId(Long tokenSetId) {
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

    public boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
}
