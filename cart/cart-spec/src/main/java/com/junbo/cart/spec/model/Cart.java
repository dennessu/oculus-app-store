/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.cart.common.validate.Group;
import com.junbo.cart.spec.model.item.CouponItem;
import com.junbo.cart.spec.model.item.OfferItem;
import com.junbo.common.id.CartId;
import com.junbo.common.id.UserId;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

/**
 * Created by fzhang@wan-san.com on 14-1-17.
 */
public class Cart {

    @JsonProperty("self")
    @NotNull(groups = Group.CartMerge.class)
    private CartId id;

    @JsonProperty("user")
    @NotNull(groups = Group.CartMerge.class)
    private UserId user;

    private String clientId;

    @JsonIgnore
    private Boolean userLoggedIn;

    @NotNull(groups = Group.CartCreate.class)
    @NotEmpty(groups = Group.CartCreate.class)
    @Size(min=1, max=80, groups = Group.CartCreate.class)
    private String cartName;

    private Long resourceAge;

    private Date createdTime;

    private Date updatedTime;

    @Valid
    private List<OfferItem> offers;

    @Valid
    private List<CouponItem> coupons;

    public CartId getId() {
        return id;
    }

    public void setId(CartId id) {
        this.id = id;
    }

    public UserId getUser() {
        return user;
    }

    public void setUser(UserId user) {
        this.user = user;
    }

    @JsonIgnore
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Boolean getUserLoggedIn() {
        return userLoggedIn;
    }

    public void setUserLoggedIn(Boolean userLoggedIn) {
        this.userLoggedIn = userLoggedIn;
    }

    public String getCartName() {
        return cartName;
    }

    public void setCartName(String cartName) {
        this.cartName = cartName;
    }

    public Long getResourceAge() {
        return resourceAge;
    }

    public void setResourceAge(Long resourceAge) {
        this.resourceAge = resourceAge;
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

    public List<OfferItem> getOffers() {
        return offers;
    }

    public void setOffers(List<OfferItem> offers) {
        this.offers = offers;
    }

    public List<CouponItem> getCoupons() {
        return coupons;
    }

    public void setCoupons(List<CouponItem> coupons) {
        this.coupons = coupons;
    }
}
