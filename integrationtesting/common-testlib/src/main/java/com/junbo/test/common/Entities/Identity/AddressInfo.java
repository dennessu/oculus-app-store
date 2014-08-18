/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.Entities.Identity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.junbo.test.common.Entities.enums.Country;

/**
 * Created by weiyu_000 on 8/15/14.
 */
public class AddressInfo {

    public String getSubCountry() {
        return subCountry;
    }

    public void setSubCountry(String subCountry) {
        this.subCountry = subCountry;
    }

    public String getStreet1() {
        return street1;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
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

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    private String city;
    private String postalCode;
    private Country country;
    private String subCountry;
    private String street1;
    private boolean isDefault;

    public JsonNode getJsonString() throws Exception {
        String str = String.format("{\"subCountry\":\"%s\",\"street1\":\"%s\"," +
                "\"city\":\"%s\",\"postalCode\":\"%s\"," +
                "\"country\":{\"id\":\"%s\"}}", subCountry, street1, city, postalCode, country.toString());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode value = mapper.readTree(str);
        return value;
    }

    public static JsonNode getAddressJsonNode() throws Exception {
        AddressInfo address = AddressInfo.getRandomAddressInfo();
        return address.getJsonString();
    }

    public static AddressInfo getRandomAddressInfo() {
        AddressInfo address = new AddressInfo();
        address.setSubCountry("TX");
        address.setStreet1("800 West Campbell Road");
        address.setCity("Richardson");
        address.setPostalCode("75080");
        address.setCountry(Country.DEFAULT);
        address.isDefault = true;
        return address;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

}
