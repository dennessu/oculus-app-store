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

    public static Address DefaultAddress() throws Exception {
        Address address = new Address();
        address.setCity("Irvine");
        CountryId countryId = new CountryId();
        countryId.setValue("US");
        address.setCountryId(countryId);
        address.setFirstName(RandomHelper.randomAlphabetic(10));
        address.setLastName(RandomHelper.randomAlphabetic(10));
        address.setPhoneNumber("16018984661");
        address.setPostalCode("92612");
        address.setStreet1("19800 MacArthur Blvd");
        return address;
    }

    public static Country DefaultCountry() throws Exception {
        Country country = new Country();
        country.setCountryCode("US");
        CurrencyId currencyId = new CurrencyId();
        currencyId.setValue("USD");
        country.setDefaultCurrency(currencyId);
        LocaleId localeId = new LocaleId();
        localeId.setValue("en_US");
        country.setDefaultLocale(localeId);
        Map<String, JsonNode> locales = new HashMap<>();
        LocaleName localeName = new LocaleName();
        localeName.setShortName("USD_SHORT");
        localeName.setLongName("USD_LONG");
        locales.put("en_US", JsonHelper.ObjectToJsonNode(localeName));
        country.setLocales(locales);
        List<RatingBoardId> ratingBoardIds = new ArrayList<>();
        country.setRatingBoardId(ratingBoardIds);
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
        currency.setCurrencyCode("USD");
        currency.setDecimalSymbol(".");
        currency.setDigitGroupingLength(3);
        currency.setDigitGroupingSymbol(",");
        Map<String, JsonNode> locales = new HashMap<>();
        LocaleName localeName = new LocaleName();
        localeName.setShortName("USD_SHORT");
        localeName.setLongName("USD_LONG");
        locales.put("en_US", JsonHelper.ObjectToJsonNode(localeName));
        currency.setLocales(locales);
        currency.setNegativeFormat("BRACE");
        currency.setNumberAfterDecimal(2);
        currency.setSymbol("$");
        currency.setSymbolPosition("BEFORE");
        return currency;
    }

    public static Locale DefaultLocale() throws Exception {
        Locale locale = new Locale();
        locale.setLocaleCode("en_US");
        locale.setLocaleName("English(US)");
        locale.setLongName("English (United States)");
        locale.setShortName("English(US)");
        return locale;
    }
}
