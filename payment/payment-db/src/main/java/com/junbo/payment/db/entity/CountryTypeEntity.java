/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.entity;

import javax.persistence.*;
import java.util.Date;


/**
 * country entity.
 */
@Entity
@Table(name = "country_type")
public class CountryTypeEntity {

    @Id
    @Column(name = "country_type_id")
    private Integer id;

    @Column(name = "iso_3166_country_number")
    private Integer countryNum;

    @Column(name = "iso_3166_country_code")
    private String countryCode;

    @Column(name = "iso_3166_country_3_code")
    private String country3Code;

    @Column(name = "iso_3166_country_name")
    private String countryName;

    @Column(name = "phone_country_code")
    private String phoneCountryCode;

    @Column(name = "default_currency_id")
    private Integer defaultCurrencyId;

    @Column(name = "country_vat_code")
    private String countryVatCode;

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

    public Integer getCountryNum() {
        return countryNum;
    }

    public void setCountryNum(Integer countryNum) {
        this.countryNum = countryNum;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountry3Code() {
        return country3Code;
    }

    public void setCountry3Code(String country3Code) {
        this.country3Code = country3Code;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getPhoneCountryCode() {
        return phoneCountryCode;
    }

    public void setPhoneCountryCode(String phoneCountryCode) {
        this.phoneCountryCode = phoneCountryCode;
    }

    public Integer getDefaultCurrencyId() {
        return defaultCurrencyId;
    }

    public void setDefaultCurrencyId(Integer defaultCurrencyId) {
        this.defaultCurrencyId = defaultCurrencyId;
    }

    public String getCountryVatCode() {
        return countryVatCode;
    }

    public void setCountryVatCode(String countryVatCode) {
        this.countryVatCode = countryVatCode;
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
