/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.jackson.hateoas;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.enumid.CurrencyId;
import com.junbo.common.id.OrderId;
import com.junbo.common.id.UserId;
import com.junbo.common.jackson.annotation.HateoasLink;
import com.junbo.common.model.Link;

/**
 * TestEntity.
 */
public class BadTestEntity {

    private UserId userId;
    private OrderId orderId;
    private CurrencyId currencyId;

    @com.junbo.common.jackson.annotation.UserId
    private Long friendUserId;

    @com.junbo.common.jackson.annotation.OrderId
    private Long friendOrderId;

    @com.junbo.common.jackson.annotation.CurrencyId
    private String friendCurrencyId;

    // JsonProperty is not allowed in @HateoasLink.
    @JsonProperty("superSuperLink")
    @HateoasLink("/users/{userId}/orders/{orderId}/friends/{friendUserId}/{friendOrderId}/end")
    private Link superLink;

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public void setOrderId(OrderId orderId) {
        this.orderId = orderId;
    }

    public CurrencyId getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(CurrencyId currencyId) {
        this.currencyId = currencyId;
    }

    public Long getFriendUserId() {
        return friendUserId;
    }

    public void setFriendUserId(Long friendUserId) {
        this.friendUserId = friendUserId;
    }

    public Long getFriendOrderId() {
        return friendOrderId;
    }

    public void setFriendOrderId(Long friendOrderId) {
        this.friendOrderId = friendOrderId;
    }

    public String getFriendCurrencyId() {
        return friendCurrencyId;
    }

    public void setFriendCurrencyId(String friendCurrencyId) {
        this.friendCurrencyId = friendCurrencyId;
    }
}
