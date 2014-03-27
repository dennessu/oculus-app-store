/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.Entities.paymentInstruments;

import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.libs.RandomFactory;

/**
 * Created by Yunlong on 3/25/14.
 */
public class Address {
    private Long id;
    private String unitNumber;
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String city;
    private String district;
    private String state;
    private String country;
    private String postalCode;

    public Address() {
    }

    public static Address getRandomAddress(Country country) {
        Address randomAddress = new Address();
        randomAddress.setCountry(country.toString());
        //randomAddress.setAddressLine1(RandomFactory.getRandomStringOfAlphabet(6));
        randomAddress.setAddressLine1("1600 Amphitheatre Parkway");
        randomAddress.setAddressLine2(RandomFactory.getRandomStringOfAlphabet(6));
        randomAddress.setAddressLine3(RandomFactory.getRandomStringOfAlphabet(6));
        randomAddress.setPostalCode("94043");
        randomAddress.setCity("Mountain View");
        randomAddress.setState("CA");
        return randomAddress;
    }


    public Long getId() {
        return id;
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public String getAddressLine3() {
        return addressLine3;
    }

    public String getCity() {
        return city;
    }

    public String getDistrict() {
        return district;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public void setAddressLine3(String addressLine3) {
        this.addressLine3 = addressLine3;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }


}
