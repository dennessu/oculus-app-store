/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.AddressId;
import com.junbo.common.enumid.CountryId;
import com.junbo.common.id.UserId;
import com.junbo.common.model.ResourceMeta;
import com.junbo.common.util.Identifiable;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by xmchen on 14-4-15.
 */
public class Address extends ResourceMeta implements Identifiable<AddressId> {

    @ApiModelProperty(position = 1, required = true, value = "[Nullable] The id of address resource.")
    @JsonProperty("self")
    private AddressId id;

    @ApiModelProperty(position = 2, required = true, value = "The user resource.")
    @JsonProperty("user")
    private UserId userId;

    @ApiModelProperty(position = 3, required = false, value = "[Required]The street1 of address.")
    private String street1;

    @ApiModelProperty(position = 4, required = false, value = "[Nullable]The street2 of address.")
    private String street2;

    @ApiModelProperty(position = 5, required = false, value = "[Nullable]The street3 of address.")
    private String street3;

    @ApiModelProperty(position = 6, required = false, value = "The city of address.")
    private String city;

    @ApiModelProperty(position = 8, required = true, value = "The postalCode of address.")
    private String postalCode;

    @ApiModelProperty(position = 9, required = false, value = "The country name of address. ")
    private String countryName;

    @ApiModelProperty(position = 10, required = true, value = "The country resource link.")
    @JsonProperty("country")
    private CountryId countryId;

    @ApiModelProperty(position = 11, required = false, value = "The firstName of address.")
    private String firstName;

    @ApiModelProperty(position = 12, required = false, value = "The lastName of address.")
    private String lastName;

    @ApiModelProperty(position = 13, required = false, value = "The phoneNumber of address.")
    private String phoneNumber;

    @ApiModelProperty(position = 14, required = false, value = "Whether the address is well formed.")
    private Boolean isWellFormed;

    @ApiModelProperty(position = 15, required = false, value = "Whether the address is normalized.")
    private Boolean isNormalized;

    @ApiModelProperty(position = 17, required = false, value = "SubCountry.")
    private SubCountry subCountry;

    @ApiModelProperty(position = 18, required = false, value = "SubCountry name.")
    private String subCountryName;

    public AddressId getId() {
        return id;
    }

    public void setId(AddressId id) {
        this.id = id;
        support.setPropertyAssigned("id");
        support.setPropertyAssigned("self");
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
        support.setPropertyAssigned("userId");
        support.setPropertyAssigned("user");
    }

    public String getStreet1() {
        return street1;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
        support.setPropertyAssigned("street1");
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
        support.setPropertyAssigned("street2");
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
        support.setPropertyAssigned("city");
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        support.setPropertyAssigned("postalCode");
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

    public SubCountry getSubCountry() {
        return subCountry;
    }

    public void setSubCountry(SubCountry subCountry) {
        this.subCountry = subCountry;
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
        support.setPropertyAssigned("firstName");
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        support.setPropertyAssigned("lastName");
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        support.setPropertyAssigned("phoneNumber");
    }

    public Boolean getIsWellFormed() {
        return isWellFormed;
    }

    public void setIsWellFormed(Boolean isWellFormed) {
        this.isWellFormed = isWellFormed;
        support.setPropertyAssigned("isWellFormed");
    }

    public Boolean getIsNormalized() {
        return isNormalized;
    }

    public void setIsNormalized(Boolean isNormalized) {
        this.isNormalized = isNormalized;
        support.setPropertyAssigned("isNormalized");
    }

    public String getStreet3() {
        return street3;
    }

    public void setStreet3(String street3) {
        this.street3 = street3;
        support.setPropertyAssigned("street3");
    }
}
