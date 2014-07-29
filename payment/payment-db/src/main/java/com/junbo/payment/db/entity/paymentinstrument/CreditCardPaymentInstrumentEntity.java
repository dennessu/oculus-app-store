/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.entity.paymentinstrument;

import com.junbo.payment.db.entity.GenericEntity;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import javax.persistence.*;
import java.util.Date;


/**
 * credit card entity.
 */
@Entity
@Table(name = "credit_card_payment_instrument")
public class CreditCardPaymentInstrumentEntity extends GenericEntity {

    @Id
    @Column(name = "payment_instrument_id")
    private Long id;

    @Column(name = "credit_card_type_id")
    private Short ccTypeId;

    @Column(name = "expire_time", updatable = false)
    private String expireDate;

    @Column(name = "bin", updatable = false)
    private String issuerIdentificationNumber;

    @Column(name = "last_billing_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastBillingDate;

    @Column(name = "prepaid")
    private Boolean isPrepaid;

    @Column(name = "debit")
    private Boolean isDebit;

    @Column(name = "commercial")
    private Boolean isCommercial;

    @Column(name = "issue_country")
    private String issueCountry;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getShardMasterId() {
        return id;
    }

    public Short getCcTypeId() {
        return ccTypeId;
    }

    public void setCcTypeId(Short ccTypeId) {
        this.ccTypeId = ccTypeId;
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

    public Date getLastBillingDate() {
        return lastBillingDate;
    }

    public void setLastBillingDate(Date lastBillingDate) {
        this.lastBillingDate = lastBillingDate;
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

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
