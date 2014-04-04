/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.junbo.common.id.OrderId;
import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.common.id.ShippingAddressId;
import com.junbo.common.id.UserId;
import com.junbo.common.jackson.annotation.ShippingMethodId;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by chriszhu on 2/7/14.
 */
@JsonPropertyOrder(value = {
        "id", "user", "trackingUuid", "type", "status", "country", "currency",
        "tentative", "resourceAge", "originalOrder", "ratingInfo", "shippingMethod",
        "shippingAddress", "paymentInstruments", "refundOrders", "discounts", "orderItems"
})
@JsonInclude(JsonInclude.Include.NON_NULL)
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

    // expand ratingInfo to simplify oom
    private BigDecimal totalAmount;
    private BigDecimal totalTax;
    private Boolean isTaxInclusive;
    private BigDecimal totalDiscount;
    private BigDecimal totalShippingFee;
    private BigDecimal totalShippingFeeDiscount;
    private BigDecimal totalPreorderAmount;
    private BigDecimal totalPreorderTax;
    @JsonIgnore
    private Date honorUntilTime;
    @JsonIgnore
    private Date honoredTime;
    // end of ratingInfo

    // expand shippingInfo to simplify oom
    @ShippingMethodId
    private Long shippingMethod;
    private ShippingAddressId shippingAddress;
    // end of shippingInfo

    private List<PaymentInstrumentId> paymentInstruments;
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

    public Date getHonoredTime() {
        return honoredTime;
    }

    public void setHonoredTime(Date honoredTime) {
        this.honoredTime = honoredTime;
    }

    public Long getShippingMethod() {
        return shippingMethod;
    }

    public void setShippingMethod(Long shippingMethod) {
        this.shippingMethod = shippingMethod;
    }

    public ShippingAddressId getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(ShippingAddressId shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public List<PaymentInstrumentId> getPaymentInstruments() {
        return paymentInstruments;
    }

    public void setPaymentInstruments(List<PaymentInstrumentId> paymentInstruments) {
        this.paymentInstruments = paymentInstruments;
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
