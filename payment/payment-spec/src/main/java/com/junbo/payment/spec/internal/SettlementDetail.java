/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.spec.internal;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Settlement Detail POJO.
 */
public class SettlementDetail {
    private Long batchIndex;
    private String companyAccount;
    private String merchantAccount;
    private String pspReference;
    private String merchantReference;
    private String paymentMethod;
    private Date creationDate;
    private String timeZone;
    private String type;
    private String modificationReference;
    private String grossCurrency;
    private BigDecimal grossDebit;
    private BigDecimal grossCredit;
    private BigDecimal exchangeRate;
    private String netCurrency;
    private BigDecimal netDebit;
    private BigDecimal netCredit;
    private String commission;
    private String markup;
    private BigDecimal schemeFees;
    private String interchange;
    private String paymentMethodVariant;
    //skip 10 reserved fields;
    private String acquirer;
    private String modificationMerchantReference;
    private String splitSettlement;

    public Long getBatchIndex() {
        return batchIndex;
    }

    public void setBatchIndex(Long batchIndex) {
        this.batchIndex = batchIndex;
    }

    public String getCompanyAccount() {
        return companyAccount;
    }

    public void setCompanyAccount(String companyAccount) {
        this.companyAccount = companyAccount;
    }

    public String getMerchantAccount() {
        return merchantAccount;
    }

    public void setMerchantAccount(String merchantAccount) {
        this.merchantAccount = merchantAccount;
    }

    public String getPspReference() {
        return pspReference;
    }

    public void setPspReference(String pspReference) {
        this.pspReference = pspReference;
    }

    public String getMerchantReference() {
        return merchantReference;
    }

    public void setMerchantReference(String merchantReference) {
        this.merchantReference = merchantReference;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getModificationReference() {
        return modificationReference;
    }

    public void setModificationReference(String modificationReference) {
        this.modificationReference = modificationReference;
    }

    public String getGrossCurrency() {
        return grossCurrency;
    }

    public void setGrossCurrency(String grossCurrency) {
        this.grossCurrency = grossCurrency;
    }

    public BigDecimal getGrossDebit() {
        return grossDebit;
    }

    public void setGrossDebit(BigDecimal grossDebit) {
        this.grossDebit = grossDebit;
    }

    public BigDecimal getGrossCredit() {
        return grossCredit;
    }

    public void setGrossCredit(BigDecimal grossCredit) {
        this.grossCredit = grossCredit;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public String getNetCurrency() {
        return netCurrency;
    }

    public void setNetCurrency(String netCurrency) {
        this.netCurrency = netCurrency;
    }

    public BigDecimal getNetDebit() {
        return netDebit;
    }

    public void setNetDebit(BigDecimal netDebit) {
        this.netDebit = netDebit;
    }

    public BigDecimal getNetCredit() {
        return netCredit;
    }

    public void setNetCredit(BigDecimal netCredit) {
        this.netCredit = netCredit;
    }

    public String getCommission() {
        return commission;
    }

    public void setCommission(String commission) {
        this.commission = commission;
    }

    public String getMarkup() {
        return markup;
    }

    public void setMarkup(String markup) {
        this.markup = markup;
    }

    public BigDecimal getSchemeFees() {
        return schemeFees;
    }

    public void setSchemeFees(BigDecimal schemeFees) {
        this.schemeFees = schemeFees;
    }

    public String getInterchange() {
        return interchange;
    }

    public void setInterchange(String interchange) {
        this.interchange = interchange;
    }

    public String getPaymentMethodVariant() {
        return paymentMethodVariant;
    }

    public void setPaymentMethodVariant(String paymentMethodVariant) {
        this.paymentMethodVariant = paymentMethodVariant;
    }

    public String getAcquirer() {
        return acquirer;
    }

    public void setAcquirer(String acquirer) {
        this.acquirer = acquirer;
    }

    public String getModificationMerchantReference() {
        return modificationMerchantReference;
    }

    public void setModificationMerchantReference(String modificationMerchantReference) {
        this.modificationMerchantReference = modificationMerchantReference;
    }

    public String getSplitSettlement() {
        return splitSettlement;
    }

    public void setSplitSettlement(String splitSettlement) {
        this.splitSettlement = splitSettlement;
    }

}
