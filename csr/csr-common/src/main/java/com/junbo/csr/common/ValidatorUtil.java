/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.csr.common;

/**
 * Created by haomin on 14-7-7.
 */
public class ValidatorUtil {
    public static boolean isValidCountryCode(String countryCode) {
        if (countryCode == null) {
            throw new IllegalArgumentException("countryCode is null");
        }


        CountryCode cc = CountryCode.getByCode(countryCode);
        if (cc == null) {
            return false;
        }


        return true;
    }

}
