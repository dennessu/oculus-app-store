/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.entity;

import com.junbo.billing.db.BaseEntity;
import com.junbo.billing.db.ext.JSONStringUserType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * Created by xmchen on 14-1-17.
 */
@Entity
@Table(name = "balance_item")
@TypeDefs({@TypeDef(name = "json-string", typeClass = JSONStringUserType.class)})
public class BalanceItemEntity extends BaseEntity {
    @Id
    @Column(name = "balance_item_id")
    private Long id;

    @Column(name = "balance_id")
    private Long balanceId;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "order_item_id")
    private Long orderItemId;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "tax_amount")
    private BigDecimal taxAmount;

    @Column(name = "discount_amount")
    private BigDecimal discountAmount;

    @Column(name = "finance_id")
    private String financeId;

    @Column(name = "original_balance_item_id")
    private Long originalBalanceItemId;

    @Column(name = "property_set")
    @Type(type = "json-string")
    private String propertySet;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBalanceId() {
        return balanceId;
    }
    public void setBalanceId(Long balanceId) {
        this.balanceId = balanceId;
    }

    public Long getOrderItemId() {
        return orderItemId;
    }
    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }
    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getFinanceId() {
        return financeId;
    }
    public void setFinanceId(String financeId) {
        this.financeId = financeId;
    }

    public Long getOriginalBalanceItemId() {
        return originalBalanceItemId;
    }
    public void setOriginalBalanceItemId(Long originalBalanceItemId) {
        this.originalBalanceItemId = originalBalanceItemId;
    }

    public Long getOrderId() {
        return orderId;
    }
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getPropertySet() {
        return propertySet;
    }
    public void setPropertySet(String propertySet) {
        this.propertySet = propertySet;
    }
}
