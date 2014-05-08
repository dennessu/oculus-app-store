/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.enumid.CountryId;

/**
 * Created by xmchen on 14-4-15.
 */
public class Address{
    private String street1;

    private String street2;

    private String street3;

    private String city;

    private String postalCode;

    private String countryName;

    @JsonProperty("country")
    private CountryId countryId;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private Boolean isWellFormed;

    private Boolean isNormalized;

    private String subCountryCode;

    private String subCountryName;

    public String getStreet1() {
        return street1;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public CountryId getCountryId() {
        return countryId;
    }

    public void setCountryId(CountryId countryId) {
        this.countryId = countryId;
    }

    public String getSubCountryCode() {
        return subCountryCode;
    }

    public void setSubCountryCode(String subCountryCode) {
        this.subCountryCode = subCountryCode;
    }

    public String getSubCountryName() {
        return subCountryName;
    }

    public void setSubCountryName(String subCountryName) {
        this.subCountryName = subCountryName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Boolean getIsWellFormed() {
        return isWellFormed;
    }

    public void setIsWellFormed(Boolean isWellFormed) {
        this.isWellFormed = isWellFormed;
    }

    public Boolean getIsNormalized() {
        return isNormalized;
    }

    public void setIsNormalized(Boolean isNormalized) {
        this.isNormalized = isNormalized;
    }

    public String getStreet3() {
        return street3;
    }

    public void setStreet3(String street3) {
        this.street3 = street3;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        Address address = (Address)obj;
        if (this.street1 != address.street1) return false;
        if (this.street2 != address.street2) return false;
        if (this.street3 != address.street3) return false;
        if (this.city != address.city) return false;
        if (this.postalCode != address.postalCode) return false;
        if (this.countryName != address.countryName) return false;
        if (this.countryId != address.countryId) return false;
        if (this.firstName != address.firstName) return false;
        if (this.lastName != address.lastName) return false;
        if (this.phoneNumber != address.phoneNumber) return false;
        if (this.isWellFormed != address.isWellFormed) return false;
        if (this.isNormalized != address.isNormalized) return false;
        if (this.subCountryCode != address.subCountryCode) return false;
        if (this.subCountryName != address.subCountryName) return false;

        return true;
    }
}
