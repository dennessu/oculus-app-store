/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.jackson.hateoas;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.OrderId;
import com.junbo.common.id.UserId;
import com.junbo.common.jackson.annotation.HateoasLink;
import com.junbo.common.model.Link;

/**
 * TestEntity.
 */
public class TestEntity {

    private UserId userId;
    private OrderId orderId;

    @com.junbo.common.jackson.annotation.UserId
    private Long friendUserId;

    @com.junbo.common.jackson.annotation.OrderId
    private Long friendOrderId;

    @JsonProperty("superSuperLink")
    @HateoasLink("/users/{userId}/orders/{orderId}/friends/{friendUserId}/{friendOrderId}/end")
    private Link superLink;

    @HateoasLink("/users/{userId}/orders/{orderId}")
    private Link subLink1;

    @HateoasLink("/friends/{friendUserId}/{friendOrderId}")
    private Link subLink2;

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

    public Link getSuperLink() {
        return superLink;
    }

    public void setSuperLink(Link superLink) {
        this.superLink = superLink;
    }

    public Link getSubLink1() {
        return subLink1;
    }

    public void setSubLink1(Link subLink1) {
        this.subLink1 = subLink1;
    }

    public Link getSubLink2() {
        return subLink2;
    }

    public void setSubLink2(Link subLink2) {
        this.subLink2 = subLink2;
    }
}
