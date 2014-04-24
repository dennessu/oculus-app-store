/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.AddressId;
import com.junbo.common.id.UserId;
import com.junbo.common.util.Identifiable;
import com.junbo.common.model.ResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by xmchen on 14-4-15.
 */
public class Address extends ResourceMeta implements Identifiable<AddressId> {

    @ApiModelProperty(position = 1, required = true, value = "The id of address resource.")
    @JsonProperty("self")
    private AddressId id;

    @ApiModelProperty(position = 2, required = true, value = "The user resource.")
    @JsonProperty("user")
    private UserId userId;

    @ApiModelProperty(position = 3, required = false, value = "The street of address resource.")
    private String street;

    @ApiModelProperty(position = 4, required = false, value = "The street1 of address resource.")
    private String street1;

    @ApiModelProperty(position = 5, required = false, value = "The street2 of address resource.")
    private String street2;

    @ApiModelProperty(position = 6, required = false, value = "The city of address resource.")
    private String city;

    @ApiModelProperty(position = 7, required = false, value = "The state of address resource.")
    private String state;

    @ApiModelProperty(position = 8, required = true, value = "The postalCode of address resource.")
    private String postalCode;

    @ApiModelProperty(position = 9, required = true, value = "The country of address resource.")
    private String country;

    @ApiModelProperty(position = 10, required = false, value = "The firstName of address resource.")
    private String firstName;

    @ApiModelProperty(position = 11, required = false, value = "The lastName of address resource.")
    private String lastName;

    @ApiModelProperty(position = 12, required = false, value = "The phoneNumber of address resource.")
    private String phoneNumber;

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

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
        support.setPropertyAssigned("street");
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
        support.setPropertyAssigned("state");
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        support.setPropertyAssigned("postalCode");
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
        support.setPropertyAssigned("country");
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
}
