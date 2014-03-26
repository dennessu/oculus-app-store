/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.Entities.paymentInstruments;

import com.junbo.test.common.libs.RandomFactory;

/**
 * Created by Yunlong on 3/25/14.
 */
public class Phone {
    private Long id;
    private String type;
    private String country;
    private String areaCode;
    private String number;
    private String extension;

    public Phone(){}

    public static Phone getRandomPhone(){
        Phone randomPhone = new Phone();
        randomPhone.setType("Home");
        randomPhone.setNumber(RandomFactory.getRandomStringOfNumeric(10));
        return randomPhone;
    }

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getCountry() {
        return country;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public String getNumber() {
        return number;
    }

    public String getExtension() {
        return extension;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

}
