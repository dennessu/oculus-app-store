/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.entity;

import com.junbo.order.db.ValidationMessages;
import com.junbo.order.spec.model.enums.OrderStatus;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by chriszhu on 1/24/14.
 */
@Entity
@Table(name = "USER_ORDER")
public class OrderEntity extends CommonDbEntityWithDate {
    private Long orderId;
    private Long userId;
    private OrderStatus orderStatusId;
    private Boolean tentative;
    private String currency;
    private String country;
    private String locale;
    private Long shippingAddressId;
    private Long shippingNameId;
    private Long shippingPhoneId;
    private String shippingMethodId;
    private Date purchaseTime;
    private Long latestOrderRevisionId;

    // expand ratingInfo to simplify oom
    private BigDecimal totalAmount;
    private BigDecimal totalTax;
    private Boolean isTaxInclusive;
    private BigDecimal totalDiscount;
    private BigDecimal totalShippingFee;
    private BigDecimal totalShippingFeeDiscount;
    private Date honorUntilTime;
    private Date honoredTime;
    private String paymentDescription;
    // end of ratingInfo

    private String properties;
    private Boolean isAudited;

    @Id
    @Column(name = "ORDER_ID")
    public Long getOrderId() {
        return orderId;
    }
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    @Column(name = "USER_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Column(name = "CURRENCY")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
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
    public String getShippingMethodId() { return shippingMethodId; }
    public void setShippingMethodId(String shippingMethodId) { this.shippingMethodId = shippingMethodId; }

    @Column(name = "COUNTRY")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    @Column(name = "PROPERTIES")
    @Length(max=4000, message=ValidationMessages.TOO_LONG)
    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    @Column(name = "ORDER_STATUS_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    @Type(type = "com.junbo.order.db.entity.type.OrderStatusType")
    public OrderStatus getOrderStatusId() {
        return orderStatusId;
    }

    public void setOrderStatusId(OrderStatus orderStatusId) {
        this.orderStatusId = orderStatusId;
    }

    @Column(name = "IS_TENTATIVE")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Boolean getTentative() {
        return tentative;
    }

    public void setTentative(Boolean tentative) {
        this.tentative = tentative;
    }

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

    @Column(name = "HONOR_UNTIL_TIME")
    public Date getHonorUntilTime() {
        return honorUntilTime;
    }

    public void setHonorUntilTime(Date honorUntilTime) {
        this.honorUntilTime = honorUntilTime;
    }

    @Column(name = "HONORED_TIME")
    public Date getHonoredTime() {
        return honoredTime;
    }

    public void setHonoredTime(Date honoredTime) {
        this.honoredTime = honoredTime;
    }

    @Column(name = "LOCALE")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    @Column(name = "PAYMENT_DESCRIPTION")
    public String getPaymentDescription() {
        return paymentDescription;
    }

    public void setPaymentDescription(String paymentDescription) {
        this.paymentDescription = paymentDescription;
    }

    @Column(name = "PURCHASE_TIME")
    public Date getPurchaseTime() {
        return purchaseTime;
    }
    public void setPurchaseTime(Date purchaseTime) {
        this.purchaseTime = purchaseTime;
    }

    @Column(name = "LATEST_ORDER_REVISION_ID")
    public Long getLatestOrderRevisionId() {
        return latestOrderRevisionId;
    }

    public void setLatestOrderRevisionId(Long latestOrderRevisionId) {
        this.latestOrderRevisionId = latestOrderRevisionId;
    }

    @Column(name = "IS_AUDITED")
    public Boolean getIsAudited() {
        return isAudited;
    }

    public void setIsAudited(Boolean isAudited) {
        this.isAudited = isAudited;
    }

    @Override
    @Transient
    public Long getShardId() {
        return orderId;
    }
}
