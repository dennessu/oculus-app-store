/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.balance;

import com.junbo.billing.db.BaseEntity;
import com.junbo.billing.db.EntityValidationCode;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by xmchen on 14-1-17.
 */
@Entity
@Table(name = "balance")
public class BalanceEntity extends BaseEntity {
    private Long balanceId;
    private Long userId;
    private Long piId;
    private Short typeId;
    private BigDecimal totalAmount;
    private BigDecimal taxAmount;
    private Boolean taxIncluded;
    private String currency;
    private String country;
    private Date dueDate;
    private Long shippingAddressId;
    private Long originalBalanceId;
    private Integer retryCount = 0;
    private Integer declineCount = 0;
    private String requestorId;
    private String onbehalfofRequestorId;

    @Id
    @Column(name = "balance_id")
    public Long getBalanceId() {
        return balanceId;
    }
    public void setBalanceId(Long balanceId) {
        this.balanceId = balanceId;
    }

    @Column(name = "user_id")
    @NotNull(message = EntityValidationCode.MISSING_VALUE)
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Column(name = "pi_id")
    @NotNull(message = EntityValidationCode.MISSING_VALUE)
    public Long getPiId() {
        return piId;
    }
    public void setPiId(Long piId) {
        this.piId = piId;
    }

    @Column(name = "total_amount")
    @NotNull(message = EntityValidationCode.MISSING_VALUE)
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Column(name = "tax_amount")
    @NotNull(message = EntityValidationCode.MISSING_VALUE)
    public BigDecimal getTaxAmount() {
        return taxAmount;
    }
    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    @Column(name = "tax_included")
    public Boolean getTaxIncluded() {
        return taxIncluded;
    }
    public void setTaxIncluded(Boolean taxIncluded) {
        this.taxIncluded = taxIncluded;
    }

    @Column(name = "currency")
    @NotEmpty(message = EntityValidationCode.MISSING_VALUE)
    @Length(max = 3, message = EntityValidationCode.TOO_LONG)
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Column(name = "country_code")
    @NotEmpty(message = EntityValidationCode.MISSING_VALUE)
    @Length(max = 2, message = EntityValidationCode.TOO_LONG)
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }

    @Column(name = "due_date")
    public Date getDueDate() {
        return dueDate;
    }
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    @Column(name = "retry_count")
    public Integer getRetryCount() {
        return retryCount;
    }
    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    @Column(name = "decline_count")
    public Integer getDeclineCount() {
        return declineCount;
    }
    public void setDeclineCount(Integer declineCount) {
        this.declineCount = declineCount;
    }

    @Column(name = "requestor_id")
    @NotEmpty(message = EntityValidationCode.MISSING_VALUE)
    @Length(max=64, message= EntityValidationCode.TOO_LONG)
    public String getRequestorId() {
        return requestorId;
    }
    public void setRequestorId(String requestorId) {
        this.requestorId = requestorId;
    }

    @Column(name = "onbehalfof_requestor_id")
    @Length(max=64, message= EntityValidationCode.TOO_LONG)
    public String getOnbehalfofRequestorId() {
        return onbehalfofRequestorId;
    }
    public void setOnbehalfofRequestorId(String onbehalfofRequestorId) {
        this.onbehalfofRequestorId = onbehalfofRequestorId;
    }

    @Column(name = "balance_type_id")
    @NotNull(message = EntityValidationCode.MISSING_VALUE)
    public Short getTypeId() {
        return typeId;
    }
    public void setTypeId(Short typeId) {
        this.typeId = typeId;
    }

    @Column(name = "shipping_address_id")
    public Long getShippingAddressId() {
        return shippingAddressId;
    }
    public void setShippingAddressId(Long shippingAddressId) {
        this.shippingAddressId = shippingAddressId;
    }

    @Column(name = "original_balance_id")
    public Long getOriginalBalanceId() {
        return originalBalanceId;
    }
    public void setOriginalBalanceId(Long originalBalanceId) {
        this.originalBalanceId = originalBalanceId;
    }
}
