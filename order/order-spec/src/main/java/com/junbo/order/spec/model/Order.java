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
import com.junbo.common.enumid.CountryId;
import com.junbo.common.enumid.CurrencyId;
import com.junbo.common.enumid.LocaleId;
import com.junbo.common.id.OrderId;
import com.junbo.common.id.ShippingAddressId;
import com.junbo.common.id.UserId;
import com.junbo.common.jackson.annotation.ShippingMethodId;
import com.junbo.common.model.Link;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by chriszhu on 2/7/14.
 */
@JsonPropertyOrder(value = {
        "id", "user", "status", "country", "currency", "locale",
        "tentative", "resourceAge", "ratingInfo", "shippingMethod",
        "shippingAddress", "paymentInstruments", "refundOrders", "discounts", "orderItems"
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Order extends BaseOrderResource {
    @ApiModelProperty(required = true, position = 10, value = "[Client Immutable] The order id.")
    @JsonProperty("self")
    private OrderId id;

    @ApiModelProperty(required = true, position = 20, value = "The user id.")
    private UserId user;

    @ApiModelProperty(required = true, position = 30, value = "Whether it's a tentative order.")
    private Boolean tentative;

    @ApiModelProperty(required = true, position = 40, value = "[Client Immutable] The Order Status. " +
            "The state diagram is here: https://www.lucidchart.com/documents/edit/4bf4b274-532a-b700-bdd9-6da00a009107",
            allowableValues = "OPEN, PENDING_CHARGE, PENDING_FULFILL, CHARGED, FULFILLED, " +
                    "COMPLETED, FAILED, CANCELED, REFUNDED, PREORDERED, ERROR")
    private String status;

    @ApiModelProperty(required = true, position = 50, value = "The order purchased country.")
    @JsonProperty("countryOfPurchase")
    private CountryId country;

    @ApiModelProperty(required = true, position = 60, value = "The order currency.")
    private CurrencyId currency;

    @ApiModelProperty(required = true, position = 70, value = "The order locale.")
    private LocaleId locale;

    // expand ratingInfo to simplify oom
    @ApiModelProperty(required = true, position = 80, value = "[Client Immutable] The order total amount.")
    private BigDecimal totalAmount;

    @ApiModelProperty(required = true, position = 90, value = "[Client Immutable] The order total tax.")
    private BigDecimal totalTax;

    @JsonProperty("isTaxIncluded")
    @ApiModelProperty(required = true, position = 100, value = "[Client Immutable] Whether the tax " +
            "is included in the total amount.")
    private Boolean isTaxInclusive;

    @ApiModelProperty(required = true, position = 110, value = "[Client Immutable] The order total discount amount.")
    private BigDecimal totalDiscount;

    @ApiModelProperty(required = true, position = 120, value = "[Client Immutable] The order total shipping fee.")

    private BigDecimal totalShippingFee;
    @ApiModelProperty(required = true, position = 130, value = "[Client Immutable] The order total shipping " +
            "fee discount amount.")
    private BigDecimal totalShippingFeeDiscount;

    @JsonIgnore
    private Date honorUntilTime;

    @JsonIgnore
    private Date honoredTime;
    // end of ratingInfo

    // expand shippingInfo to simplify oom
    @ShippingMethodId
    @ApiModelProperty(required = true, position = 75, value = "The shipping method. Required for physical goods. " +
            "It might be null if there is no shipping method at this time.")
    private Long shippingMethod;
    @ApiModelProperty(required = true, position = 76, value = "The shipping address. Required for physical goods. " +
            "It might be null if there is no shipping address at this time.")
    private ShippingAddressId shippingAddress;
    // end of shippingInfo

    @ApiModelProperty(required = true, position = 150, value = "The payments instruments. " +
            "Required if the order is not free. " +
            "It might be empty if there is no payments instruments at this time.")
    private List<PaymentInfo> payments;

    @ApiModelProperty(required = true, position = 160, value = "The discounts. " +
            "It might be empty if there is no discounts at this time.")
    private List<Discount> discounts;
    @ApiModelProperty(required = true, position = 140, value = "The order items. ")
    private List<OrderItem> orderItems;

    @ApiModelProperty(required = true, position = 170, value = "[Client Immutable]]The link to the order events. ")
    private Link orderEvents;

    @ApiModelProperty(required = true, position = 180, value = "[Client Immutable]]The link to the balances. ")
    private Link balances;

    // urls for web payment
    private String successRedirectUrl;
    private String cancelRedirectUrl;
    private String providerConfirmUrl;
    // end of urls

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public CountryId getCountry() {
        return country;
    }

    public void setCountry(CountryId country) {
        this.country = country;
    }

    public CurrencyId getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyId currency) {
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

    public LocaleId getLocale() {
        return locale;
    }

    public void setLocale(LocaleId locale) {
        this.locale = locale;
    }


    public List<PaymentInfo> getPayments() {
        return payments;
    }

    public void setPayments(List<PaymentInfo> payments) {
        this.payments = payments;
    }

    public Link getOrderEvents() {
        return orderEvents;
    }

    public void setOrderEvents(Link orderEvents) {
        this.orderEvents = orderEvents;
    }

    public Link getBalances() {
        return balances;
    }

    public void setBalances(Link balances) {
        this.balances = balances;
    }

    public String getSuccessRedirectUrl() {
        return successRedirectUrl;
    }

    public void setSuccessRedirectUrl(String successRedirectUrl) {
        this.successRedirectUrl = successRedirectUrl;
    }

    public String getCancelRedirectUrl() {
        return cancelRedirectUrl;
    }

    public void setCancelRedirectUrl(String cancelRedirectUrl) {
        this.cancelRedirectUrl = cancelRedirectUrl;
    }

    public String getProviderConfirmUrl() {
        return providerConfirmUrl;
    }

    public void setProviderConfirmUrl(String providerConfirmUrl) {
        this.providerConfirmUrl = providerConfirmUrl;
    }
}
