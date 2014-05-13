/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.common.id.UserId;
import com.junbo.common.util.IdFormatter;
import com.junbo.identity.spec.v1.model.*;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.JsonHelper;
import com.junbo.test.common.RandomHelper;

/**
 * @author dw
 */
public class Identity {

    public static final String DefaultIdentityEndPointV1 = ConfigHelper.getSetting("defaultIdentityEndPointV1");
    public static final String DefaultUserPwd = "1234qwerASDF";
    public static final String DefaultUserEmail = "leoltd@163.com";

    private Identity() {

    }

    public static User DefaultPostUser() throws Exception {
        String userName = RandomHelper.randomAlphabetic(15);
        User user = new User();
        user.setUsername(userName);
        user.setIsAnonymous(false);
        return (User) HttpclientHelper.SimpleJsonPost(DefaultIdentityEndPointV1 + "/users",
                JsonHelper.JsonSerializer(user),
                User.class);
    }

    public static User GetUserByUserId(UserId userId) throws Exception {
        return (User) HttpclientHelper.SimpleGet(DefaultIdentityEndPointV1 + "/users/"
                + IdFormatter.encodeId(userId), User.class);
    }

    public static Country DefaultPostCountry() throws Exception {
        Country country = IdentityModel.DefaultCountry();
        return (Country) HttpclientHelper.SimpleJsonPost(DefaultIdentityEndPointV1 + "/countries",
                JsonHelper.JsonSerializer(country),
                Country.class);
    }

    public static Country GetCountryByCountryId(String countryId) throws Exception {
        return (Country) HttpclientHelper.SimpleGet(DefaultIdentityEndPointV1 + "/countries/"
                + countryId, Country.class);
    }

    public static Currency DefaultPostCurrency() throws Exception {
        Currency currency = IdentityModel.DefaultCurrency();
        return (Currency) HttpclientHelper.SimpleJsonPost(DefaultIdentityEndPointV1 + "/currencies",
                JsonHelper.JsonSerializer(currency),
                Currency.class);
    }

    public static Locale DefaultPostLocale() throws Exception {
        Locale locale = IdentityModel.DefaultLocale();
        return (Locale) HttpclientHelper.SimpleJsonPost(DefaultIdentityEndPointV1 + "/locales",
                JsonHelper.JsonSerializer(locale),
                Locale.class);
    }

    public static UserPersonalInfo DefaultPostUserPersonalInfo(String type, JsonNode value) throws Exception {
        UserPersonalInfo userPersonalInfo = new UserPersonalInfo();
        userPersonalInfo.setType(type);
        userPersonalInfo.setValue(value);
        return (UserPersonalInfo) HttpclientHelper.SimpleJsonPost(DefaultIdentityEndPointV1 + "/personal-info",
                JsonHelper.JsonSerializer(userPersonalInfo),
                UserPersonalInfo.class);
    }

    // ****** start API sample logging ******
    public static final String MessageDefaultPostUser = "[Include In Sample][1] Description: Post_User_Default";
    public static final String MessageGetUserByUserId = "[Include In Sample][1] Description: Get_User_By_UserId";

    public static void StartLoggingAPISample(String message) {
        System.out.println(message);
    }
}
