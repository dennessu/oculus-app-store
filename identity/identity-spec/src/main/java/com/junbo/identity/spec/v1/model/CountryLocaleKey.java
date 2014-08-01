/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.junbo.common.jackson.annotation.XSSFreeString;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by liangfu on 6/12/14.
 */
public class CountryLocaleKey {

    @XSSFreeString
    @ApiModelProperty(position = 1, required = true, value = "Short name.")
    private String shortName;

    @XSSFreeString
    @ApiModelProperty(position = 2, required = true, value = "Long name.")
    private String longName;

    @XSSFreeString
    @ApiModelProperty(position = 3, required = false, value = " [Nullable] Name of the postal code in this specific country, as per our research," +
            " postal code is interperated differently in different countries. For example, in US, it's called \"ZIP code\" in en_US stays as \"ZIP code\" " +
            "in different locales;" +
            " while in Brizil, it's called \"CEP\" and stays as \"CEP\" in different locales. However, some countries require localized names, " +
            "for example, in GB, IE, NZ, AU the localization in en_US is \"Postcode\" but in other languages is the localized translation of \"Postal Code\" " +
            "(e.g., \"Code Postal\" when localized in fr_FR). In this attribute, it stores the country specific name of the postal code as well as " +
            "the translations per locale in this country. " +
            "The attribute shall be null in any country that doesn't have any postal codes. In these cases, the UI might choose to not render " +
            "a zip-code/postal-code field at all.")
    private String postalCode;

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}
