/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.entity;


import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * currency entity.
 */
@Entity
@Table(name = "currency_type")
public class CurrencyTypeEntity {

    @Id
    @Column(name = "currency_iso_num")
    private Integer id;

    @Column(name = "currency_description")
    private String currencyDes;

    @Column(name = "currency_code")
    private String currencyCode;

    @Column(name = "implied_decimals")
    private Integer impliedDecimals;

    @Column(name = "base_unit")
    private Integer baseUnit;

    @Column(name = "min_auth_amount")
    private BigDecimal minAuthAmount;

    @Column(name = "created_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTime;

    @Column(name = "created_by")
    private String createdBy;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCurrencyDes() {
        return currencyDes;
    }

    public void setCurrencyDes(String currencyDes) {
        this.currencyDes = currencyDes;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Integer getImpliedDecimals() {
        return impliedDecimals;
    }

    public void setImpliedDecimals(Integer impliedDecimals) {
        this.impliedDecimals = impliedDecimals;
    }

    public Integer getBaseUnit() {
        return baseUnit;
    }

    public void setBaseUnit(Integer baseUnit) {
        this.baseUnit = baseUnit;
    }

    public BigDecimal getMinAuthAmount() {
        return minAuthAmount;
    }

    public void setMinAuthAmount(BigDecimal minAuthAmount) {
        this.minAuthAmount = minAuthAmount;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

}
