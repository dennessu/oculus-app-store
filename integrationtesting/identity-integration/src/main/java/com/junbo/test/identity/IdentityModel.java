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
import com.junbo.identity.spec.v1.model.Currency;
import com.junbo.identity.spec.v1.model.Locale;
import com.junbo.identity.spec.v1.model.migration.OculusInput;
import com.junbo.identity.spec.v1.model.migration.ShareProfile;
import com.junbo.test.common.JsonHelper;
import com.junbo.test.common.RandomHelper;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

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
        Map<String, CountryLocaleKey> locales = new HashMap<>();
        locales.put("en_US", new CountryLocaleKey() {{
            setShortName("USD_SHORT");
            setLongName("USD_LONG");
        }});
        country.setLocales(locales);
        List<RatingBoardId> ratingBoards = new ArrayList<>();
        country.setRatingBoards(ratingBoards);
        Map<String, SubCountryLocaleKeys> subCountryMap = new HashMap<>();
        SubCountryLocaleKeys subCountry1 = new SubCountryLocaleKeys();
        subCountry1.setLocales(new HashMap<String, SubCountryLocaleKey>() {{
            put("en_US", new SubCountryLocaleKey() {{
                setShortName("US_NY_SHORT");
                setLongName("US_NY_LONG");
            }});
        }});
        SubCountryLocaleKeys subCountry2 = new SubCountryLocaleKeys();
        subCountry2.setLocales(new HashMap<String, SubCountryLocaleKey>() {{
            put("en_US", new SubCountryLocaleKey() {{
                setShortName("US_CA_SHORT");
                setLongName("US_CA_LONG");
            }});
        }});
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
        Map<String, CurrencyLocaleKey> localeKeys = new HashMap<>();
        localeKeys.put("en_US", new CurrencyLocaleKey() {{
            setShortName("USD_SHORT");
            setLongName("USD_LONG");
        }});
        currency.setLocales(localeKeys);
        currency.setMinAuthAmount(new BigDecimal(1));
        currency.setNumberAfterDecimal(2);
        currency.setSymbol("$");
        currency.setSymbolPosition("BEFORE");
        return currency;
    }

    public static Email DefaultEmail() throws Exception {
        Email email = new Email();
        email.setInfo(RandomHelper.randomAlphabetic(10) + "@163.com");
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

    public static OculusInput DefaultOculusInput() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        OculusInput input = new OculusInput();
        input.setCurrentId(RandomHelper.randomNumeric(2));
        input.setFirstName(RandomHelper.randomAlphabetic(10));
        input.setLastName(RandomHelper.randomAlphabetic(10));
        input.setEmail(RandomHelper.randomAlphabetic(10) + "@163.com");
        input.setUsername(RandomHelper.randomAlphabetic(15));
        input.setPassword("1:" + RandomHelper.randomAlphabetic(20) + ":" + RandomHelper.randomAlphabetic(30)
                + ":" + RandomHelper.randomAlphabetic(40));
        input.setGender("male");
        input.setDob(sdf.parse("1980-01-01 00:00:00"));
        input.setNickName(RandomHelper.randomAlphabetic(10));
        input.setTimezone(-8);
        input.setLanguage("en");
        input.setCreatedDate(sdf.parse("2013-02-07 05:30:32"));
        input.setUpdateDate(sdf.parse("2013-11-26 17:35:03"));
        input.setDevCenterCompany(RandomHelper.randomAlphabetic(15));
        input.setShareProfile(DefaultShareProfile());
        input.setOldPasswordHash(false);
        input.setStatus("ACTIVE");
        input.setForceResetPassword(false);
        return input;
    }

    public static ShareProfile DefaultShareProfile() throws Exception {
        ShareProfile shareProfile = new ShareProfile();
        shareProfile.setHeadline(RandomHelper.randomAlphabetic(10));
        shareProfile.setSummary(RandomHelper.randomAlphabetic(10));
        shareProfile.setUrl(RandomHelper.randomAlphabetic(10));
        return shareProfile;
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
