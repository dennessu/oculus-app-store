/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.entity;

import com.junbo.order.db.ValidationMessages;
import com.junbo.order.db.entity.enums.DiscountType;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created by chriszhu on 1/26/14.
 */
@Entity
@Table(name = "ORDER_DISCOUNT_INFO")
public class OrderDiscountInfoEntity extends CommonDbEntityWithDate {
    private Long discountInfoId;
    private Long orderId;
    private Long orderItemId;
    private DiscountType discountType;
    private BigDecimal discountRate;
    private BigDecimal discountAmount;

    @Id
    @Column(name = "ORDER_ITEM_DISCOUNT_INFO_ID")
    public Long getDiscountInfoId() {
        return discountInfoId;
    }

    public void setDiscountInfoId(Long discountInfoId) {
        this.discountInfoId = discountInfoId;
    }

    @Column(name = "ORDER_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    @Column(name = "ORDER_ITEM_ID")
    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    @Column(name = "DISCOUNT_TYPE")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    @Type(type = "com.junbo.order.db.entity.type.DiscountEnumType")
    public DiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }

    @Column(name = "DISCOUNT_RATE")
    public BigDecimal getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }

    @Column(name = "DISCOUNT_AMOUNT")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
}
