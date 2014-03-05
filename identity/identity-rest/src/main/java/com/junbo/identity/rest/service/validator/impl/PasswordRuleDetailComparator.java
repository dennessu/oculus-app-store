/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.service.validator.impl;

import com.junbo.identity.spec.model.password.PasswordRuleDetail;

import java.util.Comparator;

/**
 * Created by liangfu on 2/24/14.
 */
public class PasswordRuleDetailComparator implements Comparator<PasswordRuleDetail> {
    @Override
    public int compare(PasswordRuleDetail o1, PasswordRuleDetail o2) {
        if(!o1.getMinPasswordLength().equals(o2.getMinPasswordLength())) {
            return o1.getMinPasswordLength().compareTo(o2.getMinPasswordLength());
        }

        if(!o1.getMaxPasswordLength().equals(o2.getMaxPasswordLength())) {
            return o1.getMaxPasswordLength().compareTo(o2.getMaxPasswordLength());
        }

        if(!o1.getMinDigitalLength().equals(o2.getMinDigitalLength())) {
            return o1.getMinDigitalLength().compareTo(o2.getMinDigitalLength());
        }

        if(!o1.getMaxDigitalLength().equals(o2.getMaxDigitalLength())) {
            return o1.getMaxDigitalLength().compareTo(o2.getMaxDigitalLength());
        }

        if(!o1.getMinUpperAlphaLength().equals(o2.getMinUpperAlphaLength())) {
            return o1.getMinUpperAlphaLength().compareTo(o2.getMinUpperAlphaLength());
        }

        if(!o1.getMaxUpperAlphaLength().equals(o2.getMaxUpperAlphaLength())) {
            return o1.getMaxUpperAlphaLength().compareTo(o2.getMaxUpperAlphaLength());
        }

        if(!o1.getMaxLowerAlphaLength().equals(o2.getMaxLowerAlphaLength())) {
            return o1.getMaxLowerAlphaLength().compareTo(o2.getMaxLowerAlphaLength());
        }

        if(!o1.getMinLowerAlphaLength().equals(o2.getMinLowerAlphaLength())) {
            return o1.getMinLowerAlphaLength().compareTo(o2.getMinLowerAlphaLength());
        }

        if(!o1.getMinSpecialCharacterLength().equals(o2.getMinSpecialCharacterLength())) {
            return o1.getMinSpecialCharacterLength().compareTo(o2.getMinSpecialCharacterLength());
        }

        return o1.getMaxSpecialCharacterLength().compareTo(o2.getMaxSpecialCharacterLength());
    }
}
