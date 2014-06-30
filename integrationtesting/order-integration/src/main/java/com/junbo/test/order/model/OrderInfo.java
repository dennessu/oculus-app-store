/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.order.model;

import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Entities.enums.Currency;
import com.junbo.test.order.model.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by weiyu_000 on 6/26/14.
 */
public class OrderInfo {
    private String userId;
    private OrderStatus orderStatus;
    private boolean tentative;
    private Country country;
    private Currency currency;
    private String locale;
    private BigDecimal totalAmount;
    private BigDecimal totalTax;
    private boolean isTaxIncluded;
    private BigDecimal shippingFee;
    private BigDecimal shippingFeeDiscount;
    private String rev;
    private List<BillingHistoryInfo> billingHistories;
    private List<FulfilmentHistory> fulfilmentHistories;

    private List<PaymentInstrumentInfo> payments;
    private List<OrderItemInfo> orderItems;

    private BigDecimal taxRate;

    public OrderInfo() {
        payments = new ArrayList<>();
        orderItems = new ArrayList<>();
        billingHistories = new ArrayList<>();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public boolean isTentative() {
        return tentative;
    }

    public void setTentative(boolean tentative) {
        this.tentative = tentative;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
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

    public boolean isTaxIncluded() {
        return isTaxIncluded;
    }

    public void setTaxIncluded(boolean isTaxIncluded) {
        this.isTaxIncluded = isTaxIncluded;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(BigDecimal shippingFee) {
        this.shippingFee = shippingFee;
    }

    public BigDecimal getShippingFeeDiscount() {
        return shippingFeeDiscount;
    }

    public void setShippingFeeDiscount(BigDecimal shippingFeeDiscount) {
        this.shippingFeeDiscount = shippingFeeDiscount;
    }

    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }

    public List<PaymentInstrumentInfo> getPaymentInfos() {
        return payments;
    }

    public void setPaymentInfos(List<PaymentInstrumentInfo> paymentInfos) {
        this.payments = paymentInfos;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public List<OrderItemInfo> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemInfo> orderItems) {
        this.orderItems = orderItems;
    }

    public List<BillingHistoryInfo> getBillingHistories() {
        return billingHistories;
    }

    public void setBillingHistories(List<BillingHistoryInfo> billingHistories) {
        this.billingHistories = billingHistories;
    }

    public List<FulfilmentHistory> getFulfilmentHistories() {
        return fulfilmentHistories;
    }

    public void setFulfilmentHistories(List<FulfilmentHistory> fulfilmentHistories) {
        this.fulfilmentHistories = fulfilmentHistories;
    }
}
