/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.spec.model.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.CartItemId;

import java.util.Date;

/**
 * Created by fzhang@wan-san.com on 14-1-17.
 */
public abstract class CartItem {

    @JsonProperty("self")
    private CartItemId id;

    private Date createdTime;

    private Date updatedTime;

    public CartItemId getId() {
        return id;
    }

    public void setId(CartItemId id) {
        this.id = id;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }
}
