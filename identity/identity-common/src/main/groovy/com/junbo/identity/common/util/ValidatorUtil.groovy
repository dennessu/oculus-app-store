package com.junbo.identity.common.util

import groovy.transform.CompileStatic

/**
 * Created by minhao on 4/26/14.
 */
@CompileStatic
class ValidatorUtil {
    static boolean isValidCountryCode(String countryCode) {
        if (countryCode == null) {
            throw new IllegalArgumentException('countryCode is null')
        }

        CountryCode cc = CountryCode.getByCode(countryCode)
        if (cc == null) {
            return false
        }

        return true
    }

    static boolean isValidCurrencyCode(String currencyCode) {
        if (currencyCode == null) {
            throw new IllegalArgumentException('currencyCode is null')
        }

        try {
            java.util.Currency.getInstance(currencyCode)
        }
        catch (IllegalArgumentException e) {
            return false
        }

        return true
    }

    static boolean isValidLocale(String locale) {
        if (locale == null) {
            throw new IllegalArgumentException('locale is null')
        }

        String[] parts = locale.split('_')
        switch (parts.length) {
            case 3:
                return isValidLocale(new java.util.Locale(parts[0], parts[1], parts[2]))
            case 2:
                return isValidLocale(new java.util.Locale(parts[0], parts[1]))
            case 1:
                return isValidLocale(new java.util.Locale(parts[0]))
            default:
                throw new IllegalArgumentException("Invalid locale: $locale")
        }
    }

    static boolean isValidLocale(java.util.Locale locale) {
        try {
            return locale.ISO3Language != null && locale.ISO3Country != null;
        } catch (MissingResourceException e) {
            return false;
        }
    }
}
