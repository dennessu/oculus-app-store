/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.common.enumid.CountryId;
import com.junbo.common.enumid.CurrencyId;
import com.junbo.common.enumid.LocaleId;
import com.junbo.common.enumid.RatingBoardId;
import com.junbo.identity.spec.v1.model.*;
import com.junbo.test.common.JsonHelper;
import com.junbo.test.common.RandomHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dw
 */
public class IdentityModel {

    private IdentityModel() {

    }

    public static final String DefaultCountry = "US";
    public static final String DefaultCurrency = "USD";
    public static final String DefaultLocale = "en_US";

    public static Address DefaultAddress() throws Exception {
        Address address = new Address();
        address.setCity("Irvine");
        CountryId countryId = new CountryId();
        countryId.setValue(DefaultCountry);
        address.setCountryId(countryId);
        address.setPostalCode("92612");
        address.setStreet1("19800 MacArthur Blvd");
        return address;
    }

    public static Country DefaultCountry() throws Exception {
        Country country = new Country();
        country.setCountryCode(DefaultCountry);
        CurrencyId currencyId = new CurrencyId();
        currencyId.setValue(DefaultCurrency);
        country.setDefaultCurrency(currencyId);
        LocaleId localeId = new LocaleId();
        localeId.setValue(DefaultLocale);
        country.setDefaultLocale(localeId);
        Map<String, String> locales = new HashMap<>();
        locales.put("shortName", "USD_SHORT");
        locales.put("longName", "USD_LONG");
        country.setLocales(locales);
        List<RatingBoardId> ratingBoards = new ArrayList<>();
        country.setRatingBoards(ratingBoards);
        Map<String, SubCountry> subCountryMap = new HashMap<>();
        SubCountry subCountry1 = new SubCountry();
        subCountry1.setShortNameKey("US_NY_SHORT");
        subCountry1.setLongNameKey("US_NY_LONG");
        SubCountry subCountry2 = new SubCountry();
        subCountry2.setShortNameKey("US_CA_SHORT");
        subCountry2.setLongNameKey("US_CA_LONG");
        subCountryMap.put("NY", subCountry1);
        subCountryMap.put("CA", subCountry2);
        country.setSubCountries(subCountryMap);
        List<LocaleId> localeIds = new ArrayList<>();
        country.setSupportedLocales(localeIds);
        Map<String, JsonNode> fe = new HashMap<>();
        country.setFutureExpansion(fe);
        return country;
    }

    public static Currency DefaultCurrency() throws Exception {
        Currency currency = new Currency();
        currency.setCurrencyCode(DefaultCurrency);
        Map<String, String> localeKeys = new HashMap<>();
        localeKeys.put("shortName", "USD_SHORT");
        localeKeys.put("longName", "USD_LONG");
        currency.setLocaleKeys(localeKeys);
        currency.setNumberAfterDecimal(2);
        currency.setSymbol("$");
        currency.setSymbolPosition("BEFORE");
        return currency;
    }

    public static Email DefaultEmail() throws Exception {
        Email email = new Email();
        email.setInfo("silkcloudtest+" + RandomHelper.randomAlphabetic(8) + "@gmail.com");
        return email;
    }

    public static Locale DefaultLocale() throws Exception {
        Locale locale = new Locale();
        locale.setLocaleCode(DefaultLocale);
        locale.setLocaleName("English(US)");
        locale.setLongName("English (United States)");
        locale.setShortName("English(US)");
        return locale;
    }

    public static User DefaultUser() throws Exception {
        User user = new User();
        user.setUsername(RandomHelper.randomAlphabetic(15));
        user.setIsAnonymous(false);
        return user;
    }

    /**
     * copied from com\junbo\identity\data\identifiable\UserPersonalInfoType.groovy.
     */
    public static enum UserPersonalInfoType {
        ADDRESS,
        DOB,
        DRIVERS_LICENSE,
        EMAIL,
        GENDER,
        GOVERNMENT_ID,
        NAME,
        PASSPORT,
        PHONE,
        QQ,
        SMS,
        WHATSAPP,
        WIPED
    }

    public static UserPersonalInfo DefaultUserPersonalInfoAddress() throws Exception {
        UserPersonalInfo userPersonalInfo = new UserPersonalInfo();
        userPersonalInfo.setType(UserPersonalInfoType.ADDRESS.name());
        userPersonalInfo.setValue(JsonHelper.ObjectToJsonNode(DefaultAddress()));
        return userPersonalInfo;
    }

    public static UserPersonalInfo DefaultUserPersonalInfoEmail() throws Exception {
        UserPersonalInfo userPersonalInfo = new UserPersonalInfo();
        userPersonalInfo.setType(UserPersonalInfoType.EMAIL.name());
        userPersonalInfo.setValue(JsonHelper.ObjectToJsonNode(DefaultEmail()));
        return userPersonalInfo;
    }
}
