/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.db.entity;

import com.junbo.order.db.ValidationMessages;
import com.junbo.order.spec.model.enums.OrderItemRevisionType;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created by chriszhu on 6/11/14.
 */
@Entity
@Table(name = "ORDER_ITEM_REVISION")
public class OrderItemRevisionEntity   extends CommonDbEntityWithDate {

    private Long orderItemRevisionId;
    private Long orderItemId;
    private Long orderId;
    private OrderItemRevisionType revisionType;
    private Integer quantity;
    private Long shippingAddressId;
    private Long shippingMethodId;
    private BigDecimal totalAmount;
    private BigDecimal totalTax;
    private BigDecimal totalDiscount;
    private BigDecimal totalShippingFee;
    private BigDecimal totalShippingFeeDiscount;
    private Boolean revoked;

    @Id
    @Column(name = "ORDER_ITEM_REVISION_ID")
    public Long getOrderItemRevisionId() {
        return orderItemRevisionId;
    }
    public void setOrderItemRevisionId(Long orderItemRevisionId) {
        this.orderItemRevisionId = orderItemRevisionId;
    }

    @Column(name = "ORDER_ITEM_ID")
    public Long getOrderItemId() {
        return orderItemId;
    }
    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    @Column(name = "ORDER_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Long getOrderId() {
        return orderId;
    }
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }


    @Column(name = "REVISION_TYPE")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    @Type(type = "com.junbo.order.db.entity.type.OrderItemRevisionEnumType")
    public OrderItemRevisionType getRevisionType() {
        return revisionType;
    }
    public void setRevisionType(OrderItemRevisionType revisionType) {
        this.revisionType = revisionType;
    }

    @Column(name = "QUANTITY")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Column(name = "SHIPPING_ADDRESS_ID")
    public Long getShippingAddressId() { return shippingAddressId; }
    public void setShippingAddressId(Long shippingAddressId) { this.shippingAddressId = shippingAddressId; }

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

    @Column(name = "TOTAL_DISCOUNT")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public BigDecimal getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(BigDecimal totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    @Column(name = "TOTAL_SHIPPING_FEE")
    public BigDecimal getTotalShippingFee() {
        return totalShippingFee;
    }

    public void setTotalShippingFee(BigDecimal totalShippingFee) {
        this.totalShippingFee = totalShippingFee;
    }

    @Column(name = "TOTAL_SHIPPING_FEE_DISCOUNT")
    public BigDecimal getTotalShippingFeeDiscount() {
        return totalShippingFeeDiscount;
    }

    public void setTotalShippingFeeDiscount(BigDecimal totalShippingFeeDiscount) {
        this.totalShippingFeeDiscount = totalShippingFeeDiscount;
    }

    @Column(name = "REVOKED")
    public Boolean getRevoked() {
        return revoked;
    }

    public void setRevoked(Boolean revoked) {
        this.revoked = revoked;
    }

    @Override
    @Transient
    public Long getShardId() {
        return orderItemId;
    }
}
