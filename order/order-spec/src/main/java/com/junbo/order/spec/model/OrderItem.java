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
import com.junbo.common.id.OfferId;
import com.junbo.common.id.OrderId;
import com.junbo.common.id.OrderItemId;
import com.junbo.common.id.ShippingAddressId;
import com.junbo.common.jackson.annotation.ShippingMethodId;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by chriszhu on 2/7/14.
 */
@JsonPropertyOrder(value = {
        "id", "status", "type", "offer", "quantity", "shippingInfo",
        "unitPrice", "totalAmount", "totalDiscount", "totalTax", "isTaxInclusive", "totalPreorderAmount",
        "totalPreorderTax", "createdTime", "createdBy", "updatedTime", "updatedBy", "resourceAge",
        "fulfillmentIds", "preorderInfo", "properties"
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderItem extends BaseOrderResource {
    @JsonIgnore
    private OrderItemId orderItemId;
    @JsonIgnore
    private OrderId orderId;
    @JsonIgnore
    private String type;

    @ApiModelProperty(required = true, position = 10, value = "The offer.")
    private OfferId offer;

    @ApiModelProperty(required = true, position = 20, value = "The quantity of the offer.")
    private Integer quantity;

    @JsonIgnore
    private ShippingAddressId shippingAddress;

    @JsonIgnore
    @ShippingMethodId
    private Long shippingMethod;

    // expand ratingInfo to simplify oom
    @ApiModelProperty(required = true, position = 30, value = "[Client Immutable] The unit price of the offer.")
    private BigDecimal unitPrice;

    // expand ratingInfo to simplify oom
    @ApiModelProperty(required = true, position = 40, value = "[Client Immutable] The offer total amount.")
    private BigDecimal totalAmount;

    @ApiModelProperty(required = true, position = 50, value = "[Client Immutable] The offer total tax.")
    private BigDecimal totalTax;

    @ApiModelProperty(required = true, position = 60, value = "[Client Immutable] The offer total discount amount.")
    private BigDecimal totalDiscount;

    @JsonIgnore
    private Date honorUntilTime;
    @JsonIgnore
    private Date honoredTime;
    // end of ratingInfo

    @JsonIgnore
    private PreorderInfo preorderInfo;

    @JsonProperty("futureExpansion")
    private String properties;

    private List<OrderTaxItem> taxes;

    public OrderItemId getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(OrderItemId orderItemId) {
        this.orderItemId = orderItemId;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public void setOrderId(OrderId orderId) {
        this.orderId = orderId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public OfferId getOffer() {
        return offer;
    }

    public void setOffer(OfferId offer) {
        this.offer = offer;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public ShippingAddressId getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(ShippingAddressId shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public Long getShippingMethod() {
        return shippingMethod;
    }

    public void setShippingMethod(Long shippingMethod) {
        this.shippingMethod = shippingMethod;
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

    public BigDecimal getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(BigDecimal totalDiscount) {
        this.totalDiscount = totalDiscount;
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

    public PreorderInfo getPreorderInfo() {
        return preorderInfo;
    }

    public void setPreorderInfo(PreorderInfo preorderInfo) {
        this.preorderInfo = preorderInfo;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public List<OrderTaxItem> getTaxes() {
        return taxes;
    }

    public void setTaxes(List<OrderTaxItem> taxes) {
        this.taxes = taxes;
    }
}
