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
import com.junbo.common.cloudant.json.annotations.CloudantIgnore;
import com.junbo.common.id.OfferId;
import com.junbo.common.id.OrderId;
import com.junbo.common.id.OrderItemId;
import com.junbo.common.id.UserPersonalInfoId;
import com.junbo.common.jackson.annotation.ShippingMethodId;
import com.junbo.common.model.ResourceMetaForDualWrite;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by chriszhu on 2/7/14.
 */
@JsonPropertyOrder(value = {
        "offer", "quantity",
        "unitPrice", "totalAmount", "totalTax", "totalDiscount",
        "taxes", "fulfillmentHistories"
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderItem extends ResourceMetaForDualWrite<OrderItemId> {
    @JsonIgnore
    private OrderItemId id;

    @JsonIgnore
    private OrderId orderId;

    @JsonIgnore
    private String type;

    @ApiModelProperty(required = true, position = 10, value = "The offer.")
    private OfferId offer;

    @ApiModelProperty(required = true, position = 20, value = "The quantity of the offer.")
    private Integer quantity;

    @JsonIgnore
    private UserPersonalInfoId shippingAddress;

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
    @CloudantIgnore
    private PreorderInfo preorderInfo;

    @JsonProperty("futureExpansion")
    private String properties;

    // transient, read from billing
    @ApiModelProperty(required = true, position = 100, value = "[Client Immutable] The tax details of the item.")
    @CloudantIgnore
    private List<OrderTaxItem> taxes;

    @ApiModelProperty(required = true, position = 110, value = "[Client Immutable] The fulfillment history the item.")
    @CloudantIgnore
    private List<FulfillmentHistory> fulfillmentHistories;

    @JsonIgnore
    private Boolean isPreorder;

    @JsonIgnore
    private List<OrderItemRevision> orderItemRevisions = new ArrayList<>();

    @JsonIgnore
    private Long latestOrderItemRevisionId;

    @JsonIgnore
    private String offerName;

    @JsonIgnore
    private String offerDescription;

    @JsonIgnore
    private String offerOrganization;

    @Override
    public OrderItemId getId() {
        return id;
    }

    @Override
    public void setId(OrderItemId id) {
        this.id = id;
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

    public UserPersonalInfoId getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(UserPersonalInfoId shippingAddress) {
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

    public List<FulfillmentHistory> getFulfillmentHistories() {
        return fulfillmentHistories;
    }

    public void setFulfillmentHistories(List<FulfillmentHistory> fulfillmentHistories) {
        this.fulfillmentHistories = fulfillmentHistories;
    }

    public Boolean getIsPreorder() {
        return isPreorder;
    }

    public void setIsPreorder(Boolean isPreorder) {
        this.isPreorder = isPreorder;
    }

    public List<OrderItemRevision> getOrderItemRevisions() {
        return orderItemRevisions;
    }

    public void setOrderItemRevisions(List<OrderItemRevision> orderItemRevisions) {
        this.orderItemRevisions = orderItemRevisions;
    }

    public Long getLatestOrderItemRevisionId() {
        return latestOrderItemRevisionId;
    }

    public void setLatestOrderItemRevisionId(Long latestOrderItemRevisionId) {
        this.latestOrderItemRevisionId = latestOrderItemRevisionId;
    }

    public String getOfferName() {
        return offerName;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }

    public String getOfferDescription() {
        return offerDescription;
    }

    public void setOfferDescription(String offerDescription) {
        this.offerDescription = offerDescription;
    }

    public String getOfferOrganization() {
        return offerOrganization;
    }

    public void setOfferOrganization(String offerOrganization) {
        this.offerOrganization = offerOrganization;
    }
}
