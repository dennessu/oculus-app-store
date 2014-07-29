/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.common.jackson.annotation.CountryId;
import com.junbo.common.jackson.annotation.CurrencyId;
import com.junbo.common.jackson.annotation.XSSFreeString;
import com.junbo.payment.common.FilterIn;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Type specific details.
 */
public class TypeSpecificDetails {
    private Long id;
    @ApiModelProperty(position = 1, required = true, value = "[Client Immutable] The expire date of the PI.")
    @XSSFreeString
    private String expireDate;
    @ApiModelProperty(position = 2, required = true, value = "[Client Immutable] the IIN data of the PI.")
    @FilterIn
    private String issuerIdentificationNumber;
    @JsonIgnore
    private String encryptedCvmCode;
    //response only
    @ApiModelProperty(position = 3, required = true, value = "[Client Immutable] Last billing date for the PI.")
    @FilterIn
    private Date lastBillingDate;
    @ApiModelProperty(position = 4, required = true,
            value = "[Client Immutable] The sub-type for the PI, like VISA, MASTER-CARD,JCB etc.")
    @FilterIn
    private String creditCardType;
    @ApiModelProperty(position = 5, required = true,
            value = "[Client Immutable] Whether the PI is prepaid.")
    @FilterIn
    private Boolean prepaid;
    @ApiModelProperty(position = 6, required = true,
            value = "[Client Immutable] Whether the PI is a debit card.")
    @FilterIn
    private Boolean debit;
    @ApiModelProperty(position = 7, required = true,
            value = "[Client Immutable] Whether the PI is commercial card.")
    @FilterIn
    private Boolean commercial;
    @ApiModelProperty(position = 8, required = true,
            value = "[Client Immutable] The country resource where the PI issued.")
    @FilterIn
    @CountryId
    private String issueCountry;
    @JsonIgnore
    @XSSFreeString
    private String walletType;
    @ApiModelProperty(position = 9, required = true, value = "The currency resource of the Storde Value PI")
    @CurrencyId
    @XSSFreeString
    private String storedValueCurrency;
    @ApiModelProperty(position = 10, required = true,
            value = "[Client Immutable] The Stored Value balance of the PI.")
    @FilterIn
    private BigDecimal storedValueBalance;

    @JsonIgnore
    public Long getId() {
        return id;
    }
    @JsonIgnore
    public void setId(Long id) {
        this.id = id;
    }

    public String getCreditCardType() {
        return creditCardType;
    }

    public void setCreditCardType(String creditCardType) {
        this.creditCardType = creditCardType;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getIssuerIdentificationNumber() {
        return issuerIdentificationNumber;
    }

    public void setIssuerIdentificationNumber(String issuerIdentificationNumber) {
        this.issuerIdentificationNumber = issuerIdentificationNumber;
    }

    public String getEncryptedCvmCode() {
        return encryptedCvmCode;
    }

    public void setEncryptedCvmCode(String encryptedCvmCode) {
        this.encryptedCvmCode = encryptedCvmCode;
    }

    public Boolean getPrepaid() {
        return prepaid;
    }

    public void setPrepaid(Boolean prepaid) {
        this.prepaid = prepaid;
    }

    public Boolean getDebit() {
        return debit;
    }

    public void setDebit(Boolean debit) {
        this.debit = debit;
    }

    public Boolean getCommercial() {
        return commercial;
    }

    public void setCommercial(Boolean commercial) {
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

    public String getWalletType() {
        return walletType;
    }

    public void setWalletType(String walletType) {
        this.walletType = walletType;
    }

    public String getStoredValueCurrency() {
        return storedValueCurrency;
    }

    public void setStoredValueCurrency(String storedValueCurrency) {
        this.storedValueCurrency = storedValueCurrency;
    }

    public BigDecimal getStoredValueBalance() {
        return storedValueBalance;
    }

    public void setStoredValueBalance(BigDecimal storedValueBalance) {
        this.storedValueBalance = storedValueBalance;
    }

}
