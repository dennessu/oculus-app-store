/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.entity;

import com.junbo.order.db.ValidationMessages;
import com.junbo.order.spec.model.enums.ItemType;
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
    private String offerId;
    private BigDecimal unitPrice;
    private Integer quantity;
    private String properties;
    private Long shippingAddressId;
    private Long shippingMethodId;
    // expand ratingInfo to simplify oom
    private BigDecimal totalAmount;
    private BigDecimal totalTax;
    private BigDecimal totalDiscount;
    private BigDecimal totalShippingFee;
    private BigDecimal totalShippingFeeDiscount;
    private BigDecimal developerRevenue;
    private BigDecimal preorderAmount;
    private Long latestOrderItemRevisionId;
    private Date honorUntilTime;
    private Date honoredTime;
    private Boolean isPreorder;
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

    @Column(name = "OFFER_ID")
    @NotEmpty(message = ValidationMessages.MISSING_VALUE)
    @Length(max=128, message=ValidationMessages.TOO_LONG)
    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
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

    @Column(name = "TOTAL_DISCOUNT")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public BigDecimal getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(BigDecimal totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    @Column(name = "DEVELOPER_REVENUE")
    public BigDecimal getDeveloperRevenue() {
        return developerRevenue;
    }

    public void setDeveloperRevenue(BigDecimal developerRevenue) {
        this.developerRevenue = developerRevenue;
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

    @Column(name = "LATEST_ORDER_ITEM_REVISION_ID")
    public Long getLatestOrderItemRevisionId() {
        return latestOrderItemRevisionId;
    }

    public void setLatestOrderItemRevisionId(Long latestOrderItemRevisionId) {
        this.latestOrderItemRevisionId = latestOrderItemRevisionId;
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

    @Column(name = "IS_PREORDER")
    public Boolean getIsPreorder() {
        return isPreorder;
    }

    public void setIsPreorder(Boolean isPreorder) {
        this.isPreorder = isPreorder;
    }

    @Column(name = "PREORDER_AMOUNT")
    public BigDecimal getPreorderAmount() {
        return preorderAmount;
    }

    public void setPreorderAmount(BigDecimal preorderAmount) {
        this.preorderAmount = preorderAmount;
    }

    @Override
    @Transient
    public Long getShardId() {
        return orderItemId;
    }
}
