/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.entity;

import com.junbo.billing.db.BaseEntity;
import com.junbo.billing.db.ext.JSONStringUserType;
import com.junbo.common.util.Identifiable;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * Created by xmchen on 14-1-17.
 */
@Entity
@Table(name = "balance")
@TypeDefs({@TypeDef(name = "json-string", typeClass = JSONStringUserType.class)})
public class BalanceEntity extends BaseEntity implements Identifiable<Long> {
    @Id
    @Column(name = "balance_id")
    private Long id;

    @Column(name = "tracking_uuid")
    @Type(type = "pg-uuid")
    private UUID trackingUuid;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "pi_id")
    private Long piId;

    @Column(name = "balance_type_id")
    private Short typeId;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "tax_amount")
    private BigDecimal taxAmount;

    @Column(name = "tax_included")
    private Boolean taxIncluded;

    @Column(name = "discount_amount")
    private BigDecimal discountAmount;

    @Column(name = "tax_status_id")
    private Short taxStatusId;

    @Column(name = "currency")
    private String currency;

    @Column(name = "country_code")
    private String country;

    @Column(name = "status_id")
    private Short statusId;

    @Column(name = "due_date")
    private Date dueDate;

    @Column(name = "is_async_charge")
    private Boolean isAsyncCharge;

    @Column(name = "shipping_address_id")
    private Long shippingAddressId;

    @Column(name = "original_balance_id")
    private Long originalBalanceId;

    @Column(name = "retry_count")
    private Integer retryCount = 0;

    @Column(name = "decline_count")
    private Integer declineCount = 0;

    @Column(name = "requestor_id")
    private String requestorId;

    @Column(name = "onbehalfof_requestor_id")
    private String onbehalfofRequestorId;

    @Column(name = "property_set")
    @Type(type = "json-string")
    private String propertySet;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getTrackingUuid() {
        return trackingUuid;
    }
    public void setTrackingUuid(UUID trackingUuid) {
        this.trackingUuid = trackingUuid;
    }

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPiId() {
        return piId;
    }
    public void setPiId(Long piId) {
        this.piId = piId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }
    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public Boolean getTaxIncluded() {
        return taxIncluded;
    }
    public void setTaxIncluded(Boolean taxIncluded) {
        this.taxIncluded = taxIncluded;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Short getTaxStatusId() {
        return taxStatusId;
    }
    public void setTaxStatusId(Short taxStatusId) {
        this.taxStatusId = taxStatusId;
    }

    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }

    public Short getStatusId() {
        return statusId;
    }
    public void setStatusId(Short statusId) {
        this.statusId = statusId;
    }

    public Date getDueDate() {
        return dueDate;
    }
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Boolean getIsAsyncCharge() {
        return isAsyncCharge;
    }
    public void setIsAsyncCharge(Boolean isAsyncCharge) {
        this.isAsyncCharge = isAsyncCharge;
    }

    public Integer getRetryCount() {
        return retryCount;
    }
    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public Integer getDeclineCount() {
        return declineCount;
    }
    public void setDeclineCount(Integer declineCount) {
        this.declineCount = declineCount;
    }

    public String getRequestorId() {
        return requestorId;
    }
    public void setRequestorId(String requestorId) {
        this.requestorId = requestorId;
    }

    public String getOnbehalfofRequestorId() {
        return onbehalfofRequestorId;
    }
    public void setOnbehalfofRequestorId(String onbehalfofRequestorId) {
        this.onbehalfofRequestorId = onbehalfofRequestorId;
    }

    public Short getTypeId() {
        return typeId;
    }
    public void setTypeId(Short typeId) {
        this.typeId = typeId;
    }

    public Long getShippingAddressId() {
        return shippingAddressId;
    }
    public void setShippingAddressId(Long shippingAddressId) {
        this.shippingAddressId = shippingAddressId;
    }

    public Long getOriginalBalanceId() {
        return originalBalanceId;
    }
    public void setOriginalBalanceId(Long originalBalanceId) {
        this.originalBalanceId = originalBalanceId;
    }

    public String getPropertySet() {
        return propertySet;
    }
    public void setPropertySet(String propertySet) {
        this.propertySet = propertySet;
    }
}
