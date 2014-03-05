/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.junbo.common.id.OrderId;
import com.junbo.common.id.OrderItemId;
import com.junbo.common.jackson.annotation.OfferId;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by chriszhu on 2/7/14.
 */
@JsonPropertyOrder(value = {
        "id", "status", "type", "offer", "quantity", "shippingInfo",
        "unitPrice", "totalAmount", "totalDiscount", "totalTax", "isTaxInclusive", "totalPreorderAmount",
        "totalPreorderTax", "createdTime", "createdBy", "updatedTime", "updatedBy", "resourceAge",
        "fulfillmentIds", "preorderInfo", "sellerInfo", "federatedId", "properties"
})
public class OrderItem extends BaseModelWithDate {
    @JsonProperty("self")
    private OrderItemId id;
    private OrderId orderId;
    private String status;
    private String type;
    @OfferId
    private Long offer;
    private String offerRevision;
    private Integer quantity;
    private ShippingInfo shippingInfo;

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

    private PreorderInfo preorderInfo;
    private SellerInfo sellerInfo;
    private String federatedId;
    private String properties;

    public OrderItemId getId() {
        return id;
    }

    public void setId(OrderItemId id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getOffer() {
        return offer;
    }

    public void setOffer(Long offer) {
        this.offer = offer;
    }

    public String getOfferRevision() {
        return offerRevision;
    }

    public void setOfferRevision(String offerRevision) {
        this.offerRevision = offerRevision;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public ShippingInfo getShippingInfo() {
        return shippingInfo;
    }

    public void setShippingInfo(ShippingInfo shippingInfo) {
        this.shippingInfo = shippingInfo;
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

    public PreorderInfo getPreorderInfo() {
        return preorderInfo;
    }

    public void setPreorderInfo(PreorderInfo preorderInfo) {
        this.preorderInfo = preorderInfo;
    }

    public SellerInfo getSellerInfo() {
        return sellerInfo;
    }

    public void setSellerInfo(SellerInfo sellerInfo) {
        this.sellerInfo = sellerInfo;
    }

    public String getFederatedId() {
        return federatedId;
    }

    public void setFederatedId(String federatedId) {
        this.federatedId = federatedId;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    @JsonIgnore
    public OrderId getOrderId() {
        return orderId;
    }

    public void setOrderId(OrderId orderId) {
        this.orderId = orderId;
    }
}
