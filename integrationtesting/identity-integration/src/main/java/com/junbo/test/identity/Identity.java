/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPersonalInfoId;
import com.junbo.common.model.Results;
import com.junbo.common.util.IdFormatter;
import com.junbo.identity.spec.v1.model.*;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.JsonHelper;

/**
 * @author dw
 */
public class Identity {

    public static final String DefaultIdentityEndPointV1 = ConfigHelper.getSetting("defaultIdentityEndPointV1");
    public static final String DefaultIdentityV1CountryURI = DefaultIdentityEndPointV1 + "/countries";
    public static final String DefaultIdentityV1CurrencyURI = DefaultIdentityEndPointV1 + "/currencies";
    public static final String DefaultIdentityV1LocaleURI = DefaultIdentityEndPointV1 + "/locales";
    public static final String DefaultIdentityV1UserURI = DefaultIdentityEndPointV1 + "/users";
    public static final String DefaultIdentityV1UserPersonalInfoURI = DefaultIdentityEndPointV1 + "/personal-info";

    private Identity() {

    }

    public static Country CountryPostDefault() throws Exception {
        Country country = IdentityModel.DefaultCountry();
        return (Country) HttpclientHelper.SimpleJsonPost(
                DefaultIdentityV1CountryURI,
                JsonHelper.JsonSerializer(country),
                Country.class);
    }

    public static Country CountryGetByCountryId(String countryId) throws Exception {
        return (Country) HttpclientHelper.SimpleGet(
                DefaultIdentityV1CountryURI + "/" + countryId,
                Country.class);
    }

    public static void CountryDeleteByCountryId(String countryId) throws Exception {
        HttpclientHelper.SimpleDelete(DefaultIdentityV1CountryURI + "/" + countryId);
    }

    public static Currency CurrencyPostDefault() throws Exception {
        Currency currency = IdentityModel.DefaultCurrency();
        return (Currency) HttpclientHelper.SimpleJsonPost(
                DefaultIdentityV1CurrencyURI,
                JsonHelper.JsonSerializer(currency),
                Currency.class);
    }

    public static Currency CurrencyGetByCurrencyCode(String currencyCode) throws Exception {
        return (Currency) HttpclientHelper.SimpleGet(
                DefaultIdentityV1CurrencyURI + "/" + currencyCode,
                Currency.class);
    }

    public static void CurrencyDeleteByCurrencyCode(String currencyCode) throws Exception {
        HttpclientHelper.SimpleDelete(DefaultIdentityV1CurrencyURI + "/" + currencyCode);
    }

    public static Locale LocalePostDefault() throws Exception {
        Locale locale = IdentityModel.DefaultLocale();
        return (Locale) HttpclientHelper.SimpleJsonPost(
                DefaultIdentityV1LocaleURI,
                JsonHelper.JsonSerializer(locale),
                Locale.class);
    }

    public static Locale LocaleGetByLocaleId(String localeId) throws Exception {
        return (Locale) HttpclientHelper.SimpleGet(
                DefaultIdentityV1LocaleURI + "/" + localeId,
                Locale.class);
    }

    public static void LocaleDeleteByLocaleId(String localeId) throws Exception {
        HttpclientHelper.SimpleDelete(DefaultIdentityV1LocaleURI + "/" + localeId);
    }

    public static User UserPostDefault() throws Exception {
        User user = IdentityModel.DefaultUser();
        return (User) HttpclientHelper.SimpleJsonPost(
                DefaultIdentityV1UserURI,
                JsonHelper.JsonSerializer(user),
                User.class);
    }

    public static User UserPut(User user) throws Exception {
        return (User) HttpclientHelper.SimpleJsonPut(
                DefaultIdentityV1UserURI + "/" + IdFormatter.encodeId(user.getId()),
                JsonHelper.JsonSerializer(user),
                User.class);
    }

    public static User UserGetByUserId(UserId userId) throws Exception {
        return (User) HttpclientHelper.SimpleGet(
                DefaultIdentityV1UserURI + "/" + IdFormatter.encodeId(userId),
                User.class);
    }

    public static UserPersonalInfo UserPersonalInfoPostByType(UserId userId, IdentityModel.UserPersonalInfoType type)
            throws Exception {
        UserPersonalInfo userPersonalInfo = new UserPersonalInfo();
        switch (type) {
            case ADDRESS:
                userPersonalInfo = IdentityModel.DefaultUserPersonalInfoAddress();
                break;
            case EMAIL:
                userPersonalInfo = IdentityModel.DefaultUserPersonalInfoEmail();
                break;
            default:
        }
        userPersonalInfo.setUserId(userId);
        return (UserPersonalInfo) HttpclientHelper.SimpleJsonPost(
                DefaultIdentityV1UserPersonalInfoURI,
                JsonHelper.JsonSerializer(userPersonalInfo),
                UserPersonalInfo.class);
    }

    public static UserPersonalInfo UserPersonalInfoGetByUserPersonalInfoId(UserPersonalInfoId userPersonalInfoId)
            throws Exception {
        return (UserPersonalInfo) HttpclientHelper.SimpleGet(
                DefaultIdentityV1UserPersonalInfoURI + "/" + IdFormatter.encodeId(userPersonalInfoId),
                UserPersonalInfo.class);
    }

    public static UserPersonalInfo UserPersonalInfoGetByUserEmail(String email) throws Exception {
        JsonNode jsonNode = JsonHelper.ObjectToJsonNode((HttpclientHelper.SimpleGet(
                DefaultIdentityV1UserPersonalInfoURI + "?email=" + email, (Results.class))).getItems().get(0));
        return (UserPersonalInfo) JsonHelper.JsonNodeToObject(jsonNode, UserPersonalInfo.class);
    }

    // ****** start API sample logging ******
    public static final String MessageDefaultPostUser = "[Include In Sample][1] Description: Post_User_Default";
    public static final String MessageGetUserByUserId = "[Include In Sample][1] Description: Get_User_By_UserId";

    public static void StartLoggingAPISample(String message) {
        System.out.println(message);
    }
}
