/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.db.entity;

import com.junbo.order.db.ValidationMessages;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created by chriszhu on 6/11/14.
 */
@Entity
@Table(name = "ORDER_REVISION")
public class OrderRevisionEntity  extends CommonDbEntityWithDate {

    private Long orderRevisionId;
    private Long orderId;
    private Long shippingAddressId;
    private Long shippingNameId;
    private Long shippingPhoneId;
    private Long shippingMethodId;
    private BigDecimal totalAmount;
    private BigDecimal totalTax;
    private Boolean isTaxInclusive;
    private BigDecimal totalDiscount;
    private BigDecimal totalShippingFee;
    private BigDecimal totalShippingFeeDiscount;

    @Id
    @Column(name = "ORDER_REVISION_ID")
    public Long getOrderRevisionId() {
        return orderRevisionId;
    }
    public void setOrderRevisionId(Long orderRevisionId) {
        this.orderRevisionId = orderRevisionId;
    }

    @Column(name = "ORDER_ID")
    public Long getOrderId() {
        return orderId;
    }
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    @Column(name = "SHIPPING_ADDRESS_ID")
    public Long getShippingAddressId() { return shippingAddressId; }
    public void setShippingAddressId(Long shippingAddressId) { this.shippingAddressId = shippingAddressId; }

    @Column(name = "SHIPPING_NAME_ID")
    public Long getShippingNameId() {
        return shippingNameId;
    }
    public void setShippingNameId(Long shippingNameId) {
        this.shippingNameId = shippingNameId;
    }

    @Column(name = "SHIPPING_PHONE_ID")
    public Long getShippingPhoneId() {
        return shippingPhoneId;
    }
    public void setShippingPhoneId(Long shippingPhoneId) {
        this.shippingPhoneId = shippingPhoneId;
    }

    @Column(name = "SHIPPING_METHOD_ID")
    public Long getShippingMethodId() { return shippingMethodId; }
    public void setShippingMethodId(Long shippingMethodId) { this.shippingMethodId = shippingMethodId; }

    @Column(name = "TOTAL_AMOUNT")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Column(name = "TOTAL_TAX")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public BigDecimal getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(BigDecimal totalTax) {
        this.totalTax = totalTax;
    }

    @Column(name = "IS_TAX_INCLUSIVE")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Boolean getIsTaxInclusive() {
        return isTaxInclusive;
    }

    public void setIsTaxInclusive(Boolean isTaxInclusive) {
        this.isTaxInclusive = isTaxInclusive;
    }

    @Column(name = "TOTAL_DISCOUNT")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public BigDecimal getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(BigDecimal totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    @Column(name = "TOTAL_SHIPPING_FEE")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public BigDecimal getTotalShippingFee() {
        return totalShippingFee;
    }

    public void setTotalShippingFee(BigDecimal totalShippingFee) {
        this.totalShippingFee = totalShippingFee;
    }

    @Column(name = "TOTAL_SHIPPING_FEE_DISCOUNT")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public BigDecimal getTotalShippingFeeDiscount() {
        return totalShippingFeeDiscount;
    }

    public void setTotalShippingFeeDiscount(BigDecimal totalShippingFeeDiscount) {
        this.totalShippingFeeDiscount = totalShippingFeeDiscount;
    }

    @Override
    @Transient
    public Long getShardId() {
        return orderId;
    }
}
