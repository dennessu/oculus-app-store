/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.entity;

import com.junbo.order.db.ValidationMessages;
import com.junbo.order.db.entity.enums.ItemType;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by chriszhu on 1/26/14.
 */

@Entity
@Table(name = "ORDER_ITEM")
public class OrderItemEntity extends CommonDbEntityDeletable {

    private Long orderItemId;
    private Long orderId;
    private ItemType orderItemType;
    private String productItemId;
    private BigDecimal unitPrice;
    private Integer quantity;
    private String properties;
    private Long shippingAddressId;
    private Long shippingMethodId;
    // expand ratingInfo to simplify oom
    private BigDecimal totalAmount;
    private BigDecimal totalTax;
    private Boolean isTaxExempted;
    private BigDecimal totalDiscount;
    private Date honorUntilTime;
    private Date honoredTime;
    // end of ratingInfo


    @Id
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

    @Column(name = "ORDER_ITEM_TYPE")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    @Type(type = "com.junbo.order.db.entity.type.ItemEnumType")
    public ItemType getOrderItemType() {
        return orderItemType;
    }

    public void setOrderItemType(ItemType orderItemType) {
        this.orderItemType = orderItemType;
    }

    @Column(name = "PRODUCT_ITEM_ID")
    @NotEmpty(message = ValidationMessages.MISSING_VALUE)
    @Length(max=128, message=ValidationMessages.TOO_LONG)
    public String getProductItemId() {
        return productItemId;
    }

    public void setProductItemId(String productItemId) {
        this.productItemId = productItemId;
    }

    @Column(name = "UNIT_PRICE")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    @Column(name = "QUANTITY")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Column(name = "PROPERTIES")
    @Length(max=4000, message=ValidationMessages.TOO_LONG)
    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
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

    @Column(name = "IS_TAX_EXEMPTED")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Boolean getIsTaxExempted() {
        return isTaxExempted;
    }

    public void setIsTaxExempted(Boolean isTaxExempted) {
        this.isTaxExempted = isTaxExempted;
    }

    @Column(name = "TOTAL_DISCOUNT")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public BigDecimal getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(BigDecimal totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    @Column(name = "HONOR_UNTIL_TIME")
    public Date getHonorUntilTime() {
        return honorUntilTime;
    }

    public void setHonorUntilTime(Date honorUntilTime) {
        this.honorUntilTime = honorUntilTime;
    }

    @Column(name = "HONORED_TIME")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Date getHonoredTime() {
        return honoredTime;
    }

    public void setHonoredTime(Date honoredTime) {
        this.honoredTime = honoredTime;
    }

    @Override
    @Transient
    public Long getShardId() {
        return orderItemId;
    }
}
