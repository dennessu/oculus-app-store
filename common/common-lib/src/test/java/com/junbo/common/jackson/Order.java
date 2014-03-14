/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.jackson;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.common.jackson.annotation.UserId;

/**
 * Order.
 */
public class Order {
    @JsonIgnore
    private Long buyerId;

    @JsonIgnore
    private Long sellerId;

    @UserId
    public Long getBuyerId() {
        return buyerId;
    }

    @JsonIgnore
    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    @JsonIgnore
    public Long getSellerId() {
        return sellerId;
    }

    @UserId
    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }
}
