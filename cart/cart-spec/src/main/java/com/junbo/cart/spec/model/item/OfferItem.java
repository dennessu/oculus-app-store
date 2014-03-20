/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.spec.model.item;

import com.junbo.cart.common.validate.Group;
import com.junbo.common.id.OfferId;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Created by fzhang@wan-san.com on 14-1-17.
 */
public class OfferItem extends CartItem {

    @Valid
    @NotNull(groups = {Group.CartItem.class})
    private OfferId offer;

    @NotNull(groups = {Group.CartItem.class})
    @Min(value = 0, groups = {Group.CartItem.class})
    private Long quantity;

    private Boolean selected;

    public OfferId getOffer() {
        return offer;
    }

    public void setOffer(OfferId offer) {
        this.offer = offer;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
