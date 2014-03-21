/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.spec.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.payment.common.FilterIn;
import com.junbo.payment.common.FilterOut;

import java.util.Date;

/**
 * credit card model.
 */
public class CreditCardRequest {
    private Long id;
    private String expireDate;
    @FilterOut
    private String encryptedCvmCode;
    //response only
    @FilterIn
    private Date lastBillingDate;
    @FilterIn
    private String externalToken;
    @FilterIn
    private String type;
    @FilterIn
    private String prepaid;
    @FilterIn
    private String debit;
    @FilterIn
    private String commercial;
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

    public String getEncryptedCvmCode() {
        return encryptedCvmCode;
    }

    public void setEncryptedCvmCode(String encryptedCvmCode) {
        this.encryptedCvmCode = encryptedCvmCode;
    }

    public String getExternalToken() {
        return externalToken;
    }

    public void setExternalToken(String externalToken) {
        this.externalToken = externalToken;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPrepaid() {
        return prepaid;
    }

    public void setPrepaid(String prepaid) {
        this.prepaid = prepaid;
    }

    public String getDebit() {
        return debit;
    }

    public void setDebit(String debit) {
        this.debit = debit;
    }

    public String getCommercial() {
        return commercial;
    }

    public void setCommercial(String commercial) {
        this.commercial = commercial;
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
