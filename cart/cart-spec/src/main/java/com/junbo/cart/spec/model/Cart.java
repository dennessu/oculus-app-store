/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.cart.common.validate.Group;
import com.junbo.cart.spec.model.item.OfferItem;
import com.junbo.common.id.CartId;
import com.junbo.common.id.CouponId;
import com.junbo.common.id.UserId;
import com.junbo.common.jackson.annotation.XSSFreeString;
import com.junbo.common.model.ResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Created by fzhang@wan-san.com on 14-1-17.
 */
public class Cart extends ResourceMeta<CartId> {

    @ApiModelProperty(required = true, position = 1, value = "[Client Immutable] The shopping cart ID.")
    @JsonProperty("self")
    @NotNull(groups = Group.CartMerge.class)
    private CartId id;

    @ApiModelProperty(required = true, position = 2, value = "[Client Immutable] The user of the shopping cart.")
    @JsonProperty("user")
    @NotNull(groups = Group.CartMerge.class)
    private UserId user;

    @JsonIgnore
    private String clientId;

    @JsonIgnore
    private Boolean userLoggedIn;

    @ApiModelProperty(required = true, position = 3, value =
            "The name of the shopping cart. " +
            "The shopping cart name is unique per user. " +
            "The string length is no longer than 80 characters.")
    @NotNull(groups = Group.CartCreate.class)
    @NotEmpty(groups = Group.CartCreate.class)
    @Size(min=1, max=80, groups = Group.CartCreate.class)
    @XSSFreeString
    private String cartName;

    @ApiModelProperty(required = true, position = 4, value = "The shopping cart items.")
    @Valid
    private List<OfferItem> offers;

    @ApiModelProperty(required = true, position = 5, value = "The coupons in the shopping cart.")
    @Valid
    private List<CouponId> coupons;

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

    public List<OfferItem> getOffers() {
        return offers;
    }

    public void setOffers(List<OfferItem> offers) {
        this.offers = offers;
    }

    public List<CouponId> getCoupons() {
        return coupons;
    }

    public void setCoupons(List<CouponId> coupons) {
        this.coupons = coupons;
    }
}
