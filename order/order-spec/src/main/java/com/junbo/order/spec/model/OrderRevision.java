/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.common.id.OrderId;
import com.junbo.common.id.UserPersonalInfoId;
import com.junbo.common.jackson.annotation.ShippingMethodId;
import com.junbo.common.model.ResourceMetaForDualWrite;

import java.math.BigDecimal;

/**
 * Created by chriszhu on 6/11/14.
 */
public class OrderRevision extends ResourceMetaForDualWrite<Long> {

    @JsonIgnore
    private Long id;

    @JsonIgnore
    private OrderId orderId;

    // expand ratingInfo to simplify oom
    private BigDecimal totalAmount;

    @JsonIgnore
    private BigDecimal totalTax;

    @JsonIgnore
    private Boolean isTaxInclusive;

    @JsonIgnore
    private BigDecimal totalDiscount;

    @JsonIgnore
    private BigDecimal totalShippingFee;

    @JsonIgnore
    private BigDecimal totalShippingFeeDiscount;

    @JsonIgnore
    @ShippingMethodId
    private Long shippingMethod;

    @JsonIgnore
    private UserPersonalInfoId shippingAddress;

    @JsonIgnore
    private UserPersonalInfoId shippingToName;

    @JsonIgnore
    private UserPersonalInfoId shippingToPhone;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public void setOrderId(OrderId orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(BigDecimal totalTax) {
        this.totalTax = totalTax;
    }

    public Boolean getIsTaxInclusive() {
        return isTaxInclusive;
    }

    public void setIsTaxInclusive(Boolean isTaxInclusive) {
        this.isTaxInclusive = isTaxInclusive;
    }

    public BigDecimal getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(BigDecimal totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public BigDecimal getTotalShippingFee() {
        return totalShippingFee;
    }

    public void setTotalShippingFee(BigDecimal totalShippingFee) {
        this.totalShippingFee = totalShippingFee;
    }

    public BigDecimal getTotalShippingFeeDiscount() {
        return totalShippingFeeDiscount;
    }

    public void setTotalShippingFeeDiscount(BigDecimal totalShippingFeeDiscount) {
        this.totalShippingFeeDiscount = totalShippingFeeDiscount;
    }

    public Long getShippingMethod() {
        return shippingMethod;
    }

    public void setShippingMethod(Long shippingMethod) {
        this.shippingMethod = shippingMethod;
    }

    public UserPersonalInfoId getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(UserPersonalInfoId shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public UserPersonalInfoId getShippingToName() {
        return shippingToName;
    }

    public void setShippingToName(UserPersonalInfoId shippingToName) {
        this.shippingToName = shippingToName;
    }

    public UserPersonalInfoId getShippingToPhone() {
        return shippingToPhone;
    }

    public void setShippingToPhone(UserPersonalInfoId shippingToPhone) {
        this.shippingToPhone = shippingToPhone;
    }
}
