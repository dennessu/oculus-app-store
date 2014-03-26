/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.testing.common.Entities;

import com.junbo.testing.common.Entities.enums.Country;
import com.junbo.testing.common.libs.RandomFactory;

/**
 * Created by Yunlong on 3/26/14.
 */
public class ShippingAddressInfo {
    private String addressId;
    private String userId;
    private String street;
    private String street1;
    private String street2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String firstName;
    private String lastName;
    private String middleName;
    private String phoneNumber;
    private String description;

    public ShippingAddressInfo() {
    }

    public static ShippingAddressInfo getRandomShippingAddress(Country country) {
        ShippingAddressInfo shippingAddress = new ShippingAddressInfo();
        shippingAddress.setStreet(RandomFactory.getRandomStringOfAlphabet(10));
        shippingAddress.setCity("Redwood City");
        shippingAddress.setState("CA");
        shippingAddress.setPostalCode(RandomFactory.getRandomStringOfNumeric(5));
        shippingAddress.setCountry(country.toString());
        shippingAddress.setFirstName(RandomFactory.getRandomStringOfAlphabet(5));
        shippingAddress.setLastName(RandomFactory.getRandomStringOfAlphabetOrNumeric(5));
        shippingAddress.setPhoneNumber(RandomFactory.getRandomStringOfNumeric(10));
        return shippingAddress;
    }

    public String getAddressId() {
        return addressId;
    }

    public String getUserId() {
        return userId;
    }

    public String getStreet() {
        return street;
    }

    public String getStreet1() {
        return street1;
    }

    public String getStreet2() {
        return street2;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCountry() {
        return country;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
