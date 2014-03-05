/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.spec.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * credit card model.
 */
public class CreditCardRequest {
    private Long id;
    private Date expireDate;
    private String encryptedCvmCode;
    //response only
    private Date lastBillingDate;
    private String externalToken;
    private String type;
    private String prepaid;
    private String debit;
    private String commercial;
    private String issueCountry;
    @JsonIgnore
    public Long getId() {
        return id;
    }
    @JsonIgnore
    public void setId(Long id) {
        this.id = id;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    @JsonIgnore
    public String getEncryptedCvmCode() {
        return encryptedCvmCode;
    }
    @JsonProperty
    public void setEncryptedCvmCode(String encryptedCvmCode) {
        this.encryptedCvmCode = encryptedCvmCode;
    }

    @JsonProperty
    public String getExternalToken() {
        return externalToken;
    }
    @JsonIgnore
    public void setExternalToken(String externalToken) {
        this.externalToken = externalToken;
    }
    @JsonProperty
    public String getType() {
        return type;
    }
    @JsonIgnore
    public void setType(String type) {
        this.type = type;
    }
    @JsonProperty
    public String getPrepaid() {
        return prepaid;
    }
    @JsonIgnore
    public void setPrepaid(String prepaid) {
        this.prepaid = prepaid;
    }
    @JsonProperty
    public String getDebit() {
        return debit;
    }
    @JsonIgnore
    public void setDebit(String debit) {
        this.debit = debit;
    }
    @JsonProperty
    public String getCommercial() {
        return commercial;
    }
    @JsonIgnore
    public void setCommercial(String commercial) {
        this.commercial = commercial;
    }
    @JsonProperty
    public String getIssueCountry() {
        return issueCountry;
    }
    @JsonIgnore
    public void setIssueCountry(String issueCountry) {
        this.issueCountry = issueCountry;
    }
    @JsonProperty
    public Date getLastBillingDate() {
        return lastBillingDate;
    }
    @JsonIgnore
    public void setLastBillingDate(Date lastBillingDate) {
        this.lastBillingDate = lastBillingDate;
    }
}
