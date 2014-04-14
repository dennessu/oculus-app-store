/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.spec.model.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.common.id.CartItemId;

/**
 * Created by fzhang@wan-san.com on 14-1-17.
 */
public abstract class CartItem {

    @JsonIgnore
    private CartItemId id;

    public CartItemId getId() {
        return id;
    }

    public void setId(CartItemId id) {
        this.id = id;
    }
}
