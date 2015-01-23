/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.common.id.OfferId;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * Created by chriszhu on 6/16/14.
 */
public class RefundOrderItem {

    @ApiModelProperty(required = true, position = 10, value = "The refunded offer.")
    private OfferId offer;

    @ApiModelProperty(required = true, position = 20, value = "The quantity of the refunded offer.")
    private Integer quantity;

    @ApiModelProperty(required = true, position = 40, value = "[Client Immutable] The offer refunded amount.")
    private BigDecimal refundedAmount;

    @ApiModelProperty(required = true, position = 50, value = "[Client Immutable] The offer refunded tax.")
    private BigDecimal refundedTax;

    @JsonIgnore
    private Long orderItemId;

    private Boolean revoked;

    public OfferId getOffer() {
        return offer;
    }

    public void setOffer(OfferId offer) {
        this.offer = offer;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getRefundedAmount() {
        return refundedAmount;
    }

    public void setRefundedAmount(BigDecimal refundedAmount) {
        this.refundedAmount = refundedAmount;
    }

    public BigDecimal getRefundedTax() {
        return refundedTax;
    }

    public void setRefundedTax(BigDecimal refundedTax) {
        this.refundedTax = refundedTax;
    }

    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Boolean getRevoked() {
        return revoked;
    }

    public void setRevoked(Boolean revoked) {
        this.revoked = revoked;
    }
}
