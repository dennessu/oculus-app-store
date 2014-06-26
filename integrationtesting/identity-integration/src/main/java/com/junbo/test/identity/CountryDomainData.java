/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.common.enumid.CurrencyId;
import com.junbo.common.enumid.LocaleId;
import com.junbo.identity.spec.v1.model.Country;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dw
 */
public class CountryDomainData {

    private CountryDomainData() {

    }

    public static List<Country> Tier1CountryData() {
        List<Country> countries = new ArrayList<>();
        countries.add(Country("US", CurrencyId("USD"), LocaleId("en_US")));
        countries.add(Country("CA", CurrencyId("CAD"), LocaleId("en_CA")));
        countries.add(Country("KR", CurrencyId("KRW"), LocaleId("ko_KR")));
        countries.add(Country("TW", CurrencyId("TWD"), LocaleId("zh_TW")));
        countries.add(Country("JP", CurrencyId("JPY"), LocaleId("ja_JP")));
        countries.add(Country("HK", CurrencyId("HKD"), LocaleId("zh_HK")));
        countries.add(Country("GB", CurrencyId("GBP"), LocaleId("en_GB")));
        countries.add(Country("FR", CurrencyId("EUR"), LocaleId("fr_FR")));
        countries.add(Country("RU", CurrencyId("RUB"), LocaleId("ru_RU")));
        countries.add(Country("ES", CurrencyId("EUR"), LocaleId("es_ES")));
        countries.add(Country("NL", CurrencyId("EUR"), LocaleId("nl_NL")));
        countries.add(Country("IT", CurrencyId("EUR"), LocaleId("it_IT")));
        countries.add(Country("DE", CurrencyId("EUR"), LocaleId("de_DE")));
        countries.add(Country("SA", CurrencyId("SAR"), LocaleId("ar_SA")));
        countries.add(Country("AE", CurrencyId("AED"), LocaleId("ar_AE")));
        countries.add(Country("SG", CurrencyId("SGD"), LocaleId("en_SG")));
        countries.add(Country("MY", CurrencyId("MYR"), LocaleId("en_SG")));
        countries.add(Country("TH", CurrencyId("THB"), LocaleId("th_TH")));
        countries.add(Country("TR", CurrencyId("TRY"), LocaleId("tr_TR")));
        countries.add(Country("ID", CurrencyId("IDR"), LocaleId("en_SG")));
        countries.add(Country("AU", CurrencyId("AUD"), LocaleId("en_AU")));
        countries.add(Country("IN", CurrencyId("INR"), LocaleId("en_IN")));
        countries.add(Country("MX", CurrencyId("MXN"), LocaleId("es_MX")));
        countries.add(Country("ZA", CurrencyId("ZAR"), LocaleId("en_ZA")));
        return countries;
    }

    private static Country Country(String countryCode, CurrencyId currencyId, LocaleId localeId) {
        Country country = new Country();
        country.setCountryCode(countryCode);
        country.setDefaultCurrency(currencyId);
        country.setDefaultLocale(localeId);
        return country;
    }

    private static CurrencyId CurrencyId(String currencyId) {
        CurrencyId id = new CurrencyId();
        id.setValue(currencyId);
        return id;
    }

    private static LocaleId LocaleId(String localeId) {
        LocaleId id = new LocaleId();
        id.setValue(localeId);
        return id;
    }
}
