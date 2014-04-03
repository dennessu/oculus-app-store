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
 * token consumption.
 */
@Entity
@Table(name = "token_consumption")
public class TokenConsumptionEntity extends GenericEntity {

    @Id
    @Column(name = "consumption_id")
    private Long id;

    @Column(name = "hash_value")
    private Long hashValue;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "product")
    private Long product;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHashValue() {
        return hashValue;
    }

    public void setHashValue(Long hashValue) {
        this.hashValue = hashValue;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProduct() {
        return product;
    }

    public void setProduct(Long product) {
        this.product = product;
    }


}
