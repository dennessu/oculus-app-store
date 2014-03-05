/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.junbo.common.id.OrderId;
import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.common.id.UserId;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by chriszhu on 2/7/14.
 */
@JsonPropertyOrder(value = {
        "id", "user", "trackingUuid", "type", "status", "country", "currency",
        "tentative", "resourceAge", "originalOrder", "ratingInfo", "shippingMethodId",
        "shippingAddressId", "paymentInstruments", "refundOrders", "discounts", "orderItems"
})
public class Order extends BaseModelWithDate {
    @JsonProperty("self")
    private OrderId id;
    private UserId user;
    private UUID trackingUuid;
    private String type;
    private String status;
    private String country;
    private String currency;
    private Boolean tentative;
    private OrderId originalOrder;

    // expand ratingInfo to simplify oom
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private BigDecimal totalTax;
    private Boolean isTaxInclusive;
    private BigDecimal totalDiscount;
    private BigDecimal totalShippingFeeDiscount;
    private BigDecimal totalPreorderAmount;
    private BigDecimal totalPreorderTax;
    private Date honorUntilTime;
    // end of ratingInfo

    // expand shippingInfo to simplify oom
    private Long shippingMethodId;
    private Long shippingAddressId;
    // end of shippingInfo

    private List<PaymentInstrumentId> paymentInstruments;
    private List<OrderId> refundOrders;
    private List<Discount> discounts;
    private List<OrderItem> orderItems;

    public OrderId getId() {
        return id;
    }

    public void setId(OrderId id) {
        this.id = id;
    }

    public UserId getUser() {
        return user;
    }

    public void setUser(UserId user) {
        this.user = user;
    }

    public UUID getTrackingUuid() {
        return trackingUuid;
    }

    public void setTrackingUuid(UUID trackingUuid) {
        this.trackingUuid = trackingUuid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Boolean getTentative() {
        return tentative;
    }

    public void setTentative(Boolean tentative) {
        this.tentative = tentative;
    }

    public OrderId getOriginalOrder() {
        return originalOrder;
    }

    public void setOriginalOrder(OrderId originalOrder) {
        this.originalOrder = originalOrder;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
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

    public BigDecimal getTotalShippingFeeDiscount() {
        return totalShippingFeeDiscount;
    }

    public void setTotalShippingFeeDiscount(BigDecimal totalShippingFeeDiscount) {
        this.totalShippingFeeDiscount = totalShippingFeeDiscount;
    }

    public BigDecimal getTotalPreorderAmount() {
        return totalPreorderAmount;
    }

    public void setTotalPreorderAmount(BigDecimal totalPreorderAmount) {
        this.totalPreorderAmount = totalPreorderAmount;
    }

    public BigDecimal getTotalPreorderTax() {
        return totalPreorderTax;
    }

    public void setTotalPreorderTax(BigDecimal totalPreorderTax) {
        this.totalPreorderTax = totalPreorderTax;
    }

    public Date getHonorUntilTime() {
        return honorUntilTime;
    }

    public void setHonorUntilTime(Date honorUntilTime) {
        this.honorUntilTime = honorUntilTime;
    }

    public Long getShippingMethodId() {
        return shippingMethodId;
    }

    public void setShippingMethodId(Long shippingMethodId) {
        this.shippingMethodId = shippingMethodId;
    }

    public Long getShippingAddressId() {
        return shippingAddressId;
    }

    public void setShippingAddressId(Long shippingAddressId) {
        this.shippingAddressId = shippingAddressId;
    }

    public List<PaymentInstrumentId> getPaymentInstruments() {
        return paymentInstruments;
    }

    public void setPaymentInstruments(List<PaymentInstrumentId> paymentInstruments) {
        this.paymentInstruments = paymentInstruments;
    }

    public List<OrderId> getRefundOrders() {
        return refundOrders;
    }

    public void setRefundOrders(List<OrderId> refundOrderIds) {
        this.refundOrders = refundOrderIds;
    }

    public List<Discount> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(List<Discount> discounts) {
        this.discounts = discounts;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
