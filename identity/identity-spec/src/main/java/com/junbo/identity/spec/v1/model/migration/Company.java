/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model.migration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.jackson.annotation.XSSFreeString;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by liangfu on 6/18/14.
 */
public class Company {
    @ApiModelProperty(position = 1, required = true, value = "The company id in oculus database.")
    @JsonProperty("id")
    private Long companyId;

    @ApiModelProperty(position = 2, required = true, value = "The company name in oculus database.")
    @XSSFreeString
    private String name;

    @ApiModelProperty(position = 3, required = false, value = "The company address in oculus database.")
    @XSSFreeString
    private String address;

    @ApiModelProperty(position = 4, required = false, value = "The company city in oculus database.")
    @XSSFreeString
    private String city;

    @ApiModelProperty(position = 5, required = false, value = "The company state in oculus database.")
    @XSSFreeString
    private String state;

    @ApiModelProperty(position = 6, required = false, value = "The company postalcode in oculus database.")
    @XSSFreeString
    private String postalCode;

    @ApiModelProperty(position = 7, required = false, value = "The company country in oculus database.")
    @XSSFreeString
    private String country;

    @ApiModelProperty(position = 8, required = false, value = "The company phone in oculus database.")
    @XSSFreeString
    private String phone;

    @ApiModelProperty(position = 9, required = false, value = "The company type in oculus database, it must be [CORPORATE, INDIVIDUAL]")
    @XSSFreeString
    private String type;

    @ApiModelProperty(position = 10, required = false, value = "Whether the user is the administrator of the company.")
    private Boolean isAdmin;

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
}
