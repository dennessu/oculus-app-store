/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.entity;

import com.junbo.order.db.ValidationMessages;
import com.junbo.order.db.entity.enums.OrderStatus;
import com.junbo.order.db.entity.enums.OrderType;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Created by chriszhu on 1/24/14.
 */
@Entity
@Table(name = "USER_ORDER")
public class OrderEntity extends CommonDbEntityWithDate {
    private Long orderId;
    private Long userId;
    private OrderStatus orderStatusId;
    private Long originalOrderId;
    private OrderType orderTypeId;
    private String currency;
    private String country;
    private UUID trackingUuid;
    private Long shippingAddressId;
    private Short shippingMethodId;
    private String properties;

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

    @Column(name = "ORIGINAL_ORDER_ID")
    public Long getOriginalOrderId() {
        return originalOrderId;
    }
    public void setOriginalOrderId(Long originalOrderId) {
        this.originalOrderId = originalOrderId;
    }

    @Column(name = "ORDER_TYPE_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    @Type(type = "com.junbo.order.db.entity.type.OrderEnumType")
    public OrderType getOrderTypeId() {
        return orderTypeId;
    }
    public void setOrderTypeId(OrderType orderTypeId) {
        this.orderTypeId = orderTypeId;
    }

    @Column(name = "CURRENCY")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Column(name = "TRACKING_UUID")
    @Type(type = "pg-uuid")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public UUID getTrackingUuid() {
        return trackingUuid;
    }
    public void setTrackingUuid(UUID trackingUuid) {
        this.trackingUuid = trackingUuid;
    }

    @Column(name = "SHIPPING_ADDRESS_ID")
    public Long getShippingAddressId() { return shippingAddressId; }
    public void setShippingAddressId(Long shippingAddressId) { this.shippingAddressId = shippingAddressId; }

    @Column(name = "SHIPPING_METHOD_ID")
    public Short getShippingMethodId() { return shippingMethodId; }
    public void setShippingMethodId(Short shippingMethodId) { this.shippingMethodId = shippingMethodId; }

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
}
