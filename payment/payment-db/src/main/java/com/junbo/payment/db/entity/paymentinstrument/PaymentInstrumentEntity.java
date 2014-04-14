/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.entity.paymentinstrument;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.junbo.payment.db.entity.GenericEntity;
import com.junbo.payment.spec.enums.PIType;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;


/**
 * payment instrument entity.
 */
@Entity
@Table(name = "payment_instrument")
public class PaymentInstrumentEntity extends GenericEntity {

    @Id
    @Column(name = "payment_instrument_id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "payment_instrument_type_id")
    private PIType type;

    @Column(name = "payment_instrument_holder_name")
    private String accountName;

    @Column(name = "encrypted_account_number")
    private String encryptedAccountNO;

    @Column(name = "account_number")
    private String accountNum;

    @Column(name = "revision")
    private Integer rev;

    @Column(name = "address_id")
    private Long addressId;

    @Column(name = "phone_num")
    private String phoneNum;

    @Column(name = "email")
    private String email;

    @Column(name = "relation_to_holder")
    private String relationToHolder;

    @Column(name = "active")
    private Boolean isActive;

    @Column(name = "deleted")
    private Boolean isDeleted;

    @Column(name = "last_validated_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastValidatedTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getShardMasterId() {
        return userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public PIType getType() {
        return type;
    }

    public void setType(PIType type) {
        this.type = type;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getEncryptedAccountNO() {
        return encryptedAccountNO;
    }

    public void setEncryptedAccountNO(String encryptedAccountNO) {
        this.encryptedAccountNO = encryptedAccountNO;
    }

    public String getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(String accountNum) {
        this.accountNum = accountNum;
    }

    public Integer getRev() {
        return rev;
    }

    public void setRev(Integer rev) {
        this.rev = rev;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getRelationToHolder() {
        return relationToHolder;
    }

    public void setRelationToHolder(String relationToHolder) {
        this.relationToHolder = relationToHolder;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean isDeleted() {
        return isDeleted == null ? false : isDeleted;
    }

    public void setDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Date getLastValidatedTime() {
        return lastValidatedTime;
    }

    public void setLastValidatedTime(Date lastValidatedTime) {
        this.lastValidatedTime = lastValidatedTime;
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
