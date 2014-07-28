/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.common.model.ResourceMetaForDualWrite;
import com.junbo.payment.common.FilterIn;
import com.junbo.payment.common.FilterOut;

import java.util.Date;

/**
 * credit card model.
 */
public class CreditCardDetail extends ResourceMetaForDualWrite<Long> {

    private Long id;
    private String expireDate;
    @FilterIn
    private String bin;
    @FilterOut
    private String encryptedCvmCode;
    //response only
    @FilterIn
    private Date lastBillingDate;
    @FilterIn
    private String type;
    @FilterIn
    private Boolean isPrepaid;
    @FilterIn
    private Boolean isDebit;
    @FilterIn
    private Boolean isCommercial;
    @FilterIn
    private String issueCountry;
    @JsonIgnore
    public Long getId() {
        return id;
    }
    @JsonIgnore
    public void setId(Long id) {
        this.id = id;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getBin() {
        return bin;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }

    public String getEncryptedCvmCode() {
        return encryptedCvmCode;
    }

    public void setEncryptedCvmCode(String encryptedCvmCode) {
        this.encryptedCvmCode = encryptedCvmCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getIsPrepaid() {
        return isPrepaid;
    }

    public void setIsPrepaid(Boolean prepaid) {
        this.isPrepaid = prepaid;
    }

    public Boolean getIsDebit() {
        return isDebit;
    }

    public void setIsDebit(Boolean debit) {
        this.isDebit = debit;
    }

    public Boolean getIsCommercial() {
        return isCommercial;
    }

    public void setIsCommercial(Boolean commercial) {
        this.isCommercial = commercial;
    }

    public String getIssueCountry() {
        return issueCountry;
    }

    public void setIssueCountry(String issueCountry) {
        this.issueCountry = issueCountry;
    }

    public Date getLastBillingDate() {
        return lastBillingDate;
    }

    public void setLastBillingDate(Date lastBillingDate) {
        this.lastBillingDate = lastBillingDate;
    }
}
