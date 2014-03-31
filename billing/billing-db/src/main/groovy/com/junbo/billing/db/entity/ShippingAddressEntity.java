/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.entity;

import com.junbo.billing.db.BaseEntity;
import com.junbo.billing.db.EntityValidationCode;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Created by xmchen on 14-1-20.
 */
@Entity
@Table(name = "shipping_address")
public class ShippingAddressEntity extends BaseEntity {
    private Long addressId;
    private Long userId;
    private String street;
    private String street1;
    private String street2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String companyName;
    private String firstName;
    private String middleName;
    private String lastName;
    private String phoneNumber;
    private String description;
    private Boolean deleted = false;
    private String requestorId;
    private String onbehalfofRequestorId;

    @Id
    @Column(name = "shipping_address_id")
    public Long getAddressId() {
        return addressId;
    }
    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    @Column(name = "user_id")
    @NotNull(message = EntityValidationCode.MISSING_VALUE)
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Column(name = "street")
    @NotEmpty(message = EntityValidationCode.MISSING_VALUE)
    @Length(max = 512, message = EntityValidationCode.TOO_LONG)
    public String getStreet() {
        return street;
    }
    public void setStreet(String street) {
        this.street = street;
    }

    @Column(name = "street1")
    @Length(max = 512, message = EntityValidationCode.TOO_LONG)
    public String getStreet1() {
        return street1;
    }
    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    @Column(name = "street2")
    @Length(max = 512, message = EntityValidationCode.TOO_LONG)
    public String getStreet2() {
        return street2;
    }
    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    @Column(name = "city")
    @NotEmpty(message = EntityValidationCode.MISSING_VALUE)
    @Length(max = 128, message = EntityValidationCode.TOO_LONG)
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    @Column(name = "state")
    @Length(max = 128, message = EntityValidationCode.TOO_LONG)
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }

    @Column(name = "postal_code")
    @NotEmpty(message = EntityValidationCode.MISSING_VALUE)
    @Length(max = 64, message = EntityValidationCode.TOO_LONG)
    public String getPostalCode() {
        return postalCode;
    }
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @Column(name = "country_code")
    @NotEmpty(message = EntityValidationCode.MISSING_VALUE)
    @Length(max = 2, message = EntityValidationCode.TOO_LONG)
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }

    @Column(name = "company_name")
    @Length(max = 256, message = EntityValidationCode.TOO_LONG)
    public String getCompanyName() {
        return companyName;
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    @Column(name = "first_name")
    @Length(max = 256, message = EntityValidationCode.TOO_LONG)
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(name = "middle_name")
    @Length(max = 256, message = EntityValidationCode.TOO_LONG)
    public String getMiddleName() {
        return middleName;
    }
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    @Column(name = "last_name")
    @Length(max = 256, message = EntityValidationCode.TOO_LONG)
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Column(name = "phone_number")
    @Length(max = 128, message = EntityValidationCode.TOO_LONG)
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Column(name = "description")
    @Length(max = 256, message = EntityValidationCode.TOO_LONG)
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "deleted")
    public Boolean getDeleted() {
        return deleted;
    }
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @Column(name = "requestor_id")
    @NotEmpty(message = EntityValidationCode.MISSING_VALUE)
    @Length(max=64, message= EntityValidationCode.TOO_LONG)
    public String getRequestorId() {
        return requestorId;
    }
    public void setRequestorId(String requestorId) {
        this.requestorId = requestorId;
    }

    @Column(name = "onbehalfof_requestor_id")
    @Length(max=64, message= EntityValidationCode.TOO_LONG)
    public String getOnbehalfofRequestorId() {
        return onbehalfofRequestorId;
    }
    public void setOnbehalfofRequestorId(String onbehalfofRequestorId) {
        this.onbehalfofRequestorId = onbehalfofRequestorId;
    }
}
