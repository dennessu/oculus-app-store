/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.oauth;

import com.junbo.identity.spec.v1.model.migration.Company;
import com.junbo.identity.spec.v1.model.migration.OculusInput;
import com.junbo.identity.spec.v1.model.migration.ShareProfile;
import com.junbo.identity.spec.v1.model.migration.ShareProfileAvatar;
import com.junbo.test.common.RandomHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiali_000 on 2014/8/24.
 */
public class IdentityModel {

    public static OculusInput DefaultOculusInput(String hashedPassword) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        OculusInput input = new OculusInput();
        input.setCurrentId(RandomHelper.randomLong());
        input.setFirstName(RandomHelper.randomAlphabetic(10));
        input.setLastName(RandomHelper.randomAlphabetic(10));
        input.setEmail(RandomHelper.randomAlphabetic(10) + "@163.com");
        input.setUsername(RandomHelper.randomAlphabetic(15));
        input.setPassword(hashedPassword);
        input.setGender(RandomGender());
        input.setDob(sdf.parse("1980-01-01 00:00:00"));
        input.setNickname(RandomHelper.randomAlphabetic(10));
        input.setTimezone(-8);
        input.setLanguage("en");
        input.setCreatedDate(sdf.parse("2013-02-07 05:30:32"));
        input.setUpdateDate(sdf.parse("2013-11-26 17:35:03"));
        input.setCompany(DefaultCompany());
        input.setProfile(DefaultShareProfile());
        input.setStatus("ACTIVE");
        input.setForceResetPassword(RandomHelper.randomBoolean());
        return input;
    }

    public static Company DefaultCompany() throws Exception {
        Company company = new Company();
        company.setCompanyId(RandomHelper.randomLong());
        company.setName(RandomHelper.randomAlphabetic(20));
        company.setAddress(RandomHelper.randomAlphabetic(40));
        company.setCity(RandomHelper.randomAlphabetic(30));
        company.setState(RandomHelper.randomAlphabetic(10));
        company.setPostalCode(RandomHelper.randomAlphabetic(10));
        company.setCountry("US");
        company.setPhone(RandomHelper.randomAlphabetic(30));
        company.setType(RandomMigrateCompanyType());
        company.setIsAdmin(RandomHelper.randomBoolean());
        return company;
    }

    public static ShareProfile DefaultShareProfile() throws Exception {
        ShareProfile shareProfile = new ShareProfile();
        shareProfile.setHeadline(RandomHelper.randomAlphabetic(10));
        shareProfile.setSummary(RandomHelper.randomAlphabetic(10));
        shareProfile.setUrl(RandomHelper.randomAlphabetic(10));
        ShareProfileAvatar avatar = new ShareProfileAvatar();
        avatar.setHref(RandomHelper.randomAlphabetic(100));
        shareProfile.setAvatar(avatar);
        return shareProfile;
    }

    public static String RandomGender() {
        List<Object> array = new ArrayList<>();
        array.add("MALE");
        array.add("FEMALE");
        return RandomHelper.randomValueFromList(array).toString();
    }

    public static String RandomMigrateCompanyType() {
        List<Object> array = new ArrayList<>();
        array.add("CORPORATE");
        array.add("INDIVIDUAL");
        return RandomHelper.randomValueFromList(array).toString();
    }
}
