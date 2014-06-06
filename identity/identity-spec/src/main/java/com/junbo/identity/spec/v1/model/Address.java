/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.enumid.CountryId;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by xmchen on 14-4-15.
 */
public class Address{
    @ApiModelProperty(position = 1, required = true, value = "First address line")
    private String street1;

    @ApiModelProperty(position = 2, required = false, value = "[Nullable] Second address line.")
    private String street2;

    @ApiModelProperty(position = 3, required = false, value = "[Nullable] Third address line.")
    private String street3;

    @ApiModelProperty(position = 4, required = true, value = "Name of the city.")
    private String city;

    @ApiModelProperty(position = 5, required = false, value = "[Nullable] Non-null name of the state/province in countries that have these subcountries, " +
            "otherwise null.")
    private String subCountry;

    @ApiModelProperty(position = 6, required = true, value = "Link to the address's Country resource.")
    @JsonProperty("country")
    private CountryId countryId;

    @ApiModelProperty(position = 7, required = false, value = "[Nullable] The address's postal/zip code, or null.")
    private String postalCode;

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

    public CountryId getCountryId() {
        return countryId;
    }

    public void setCountryId(CountryId countryId) {
        this.countryId = countryId;
    }

    public String getStreet3() {
        return street3;
    }

    public void setStreet3(String street3) {
        this.street3 = street3;
    }

    public String getSubCountry() {
        return subCountry;
    }

    public void setSubCountry(String subCountry) {
        this.subCountry = subCountry;
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
        if (this.countryId != address.countryId) return false;

        return true;
    }
}
