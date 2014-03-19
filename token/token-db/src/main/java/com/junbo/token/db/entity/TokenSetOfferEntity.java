/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.db.entity;

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
    private Long productId;

    @Column(name = "product_type")
    private String productType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTokenSetId() {
        return tokenSetId;
    }

    public void setTokenSetId(Long tokenSetId) {
        this.tokenSetId = tokenSetId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }
}
