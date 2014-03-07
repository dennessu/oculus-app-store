/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.entity;

import com.junbo.order.db.ValidationMessages;
import com.junbo.order.spec.model.ItemType;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created by chriszhu on 1/26/14.
 */

@Entity
@Table(name = "ORDER_ITEM")
public class OrderItemEntity extends CommonDbEntityWithDate{

    private Long orderItemId;
    private Long orderId;
    private ItemType orderItemType;
    private String productItemId;
    private String productItemVersion;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal totalPrice;
    private String federatedId;
    private String properties;

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

    @Column(name = "PRODUCT_ITEM_VERSION")
    @NotEmpty(message = ValidationMessages.MISSING_VALUE)
    @Length(max=128, message=ValidationMessages.TOO_LONG)
    public String getProductItemVersion() {
        return productItemVersion;
    }

    public void setProductItemVersion(String productItemVersion) {
        this.productItemVersion = productItemVersion;
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

    @Column(name = "TOTAL_PRICE")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Column(name = "FEDERATED_ID")
    public String getFederatedId() {
        return federatedId;
    }

    public void setFederatedId(String federatedId) {
        this.federatedId = federatedId;
    }

    @Column(name = "PROPERTIES")
    @Length(max=4000, message=ValidationMessages.TOO_LONG)
    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }
}
