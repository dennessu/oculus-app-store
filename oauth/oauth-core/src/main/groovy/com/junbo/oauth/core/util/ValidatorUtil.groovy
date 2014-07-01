package com.junbo.oauth.core.util

import com.junbo.oauth.common.CountryCode
import groovy.transform.CompileStatic

/**
 * Created by haomin on 14-4-28.
 */
@CompileStatic
class ValidatorUtil {
    static Locale convertToLocale(String locale) {
        if (locale == null) {
            throw new IllegalArgumentException('locale is null')
        }

        locale = locale.replace('-', '_')

        String[] parts = locale.split('_')
        switch (parts.length) {
            case 3:
                return new Locale(parts[0], parts[1], parts[2])
            case 2:
                return new Locale(parts[0], parts[1])
            case 1:
                return new Locale(parts[0])
            default:
                throw new IllegalArgumentException("Invalid locale: $locale")
        }
    }

    static boolean isValidLocale(String locale) {
        if (locale == null) {
            throw new IllegalArgumentException('locale is null')
        }

        locale = locale.replace('-', '_')

        String[] parts = locale.split('_')
        switch (parts.length) {
            case 3:
                return isValidLocale(new Locale(parts[0], parts[1], parts[2]))
            case 2:
                return isValidLocale(new Locale(parts[0], parts[1]))
            case 1:
                return isValidLocale(new Locale(parts[0]))
            default:
                throw new IllegalArgumentException("Invalid locale: $locale")
        }
    }

    static boolean isValidLocale(Locale locale) {
        try {
            return locale.ISO3Language != null && locale.ISO3Country != null;
        } catch (MissingResourceException e) {
            return false;
        }
    }

    static boolean isValidCountryCode(String countryCode) {
        if (countryCode == null) {
            throw new IllegalArgumentException('countryCode is null')
        }

        // Due to Oculus's requirement, this will need to hack WORLD as one new countryCode
        if (countryCode == "WORLD") {
            return true
        }

        CountryCode cc = CountryCode.getByCode(countryCode)
        if (cc == null) {
            return false
        }

        return true
    }
}
