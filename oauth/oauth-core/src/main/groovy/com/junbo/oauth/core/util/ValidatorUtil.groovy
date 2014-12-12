package com.junbo.oauth.core.util

import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.LocaleId
import com.junbo.identity.spec.v1.model.Country
import com.junbo.identity.spec.v1.option.model.CountryGetOptions
import com.junbo.identity.spec.v1.option.model.LocaleGetOptions
import com.junbo.identity.spec.v1.resource.CountryResource
import com.junbo.identity.spec.v1.resource.LocaleResource
import groovy.transform.CompileStatic

/**
 * Created by haomin on 14-4-28.
 */
@CompileStatic
class ValidatorUtil {
    static boolean isValidLocale(String locale, LocaleResource localeResource) {
        if (locale == null) {
            throw new IllegalArgumentException('locale is null')
        }

        com.junbo.identity.spec.v1.model.Locale l = localeResource.get(new LocaleId(locale), new LocaleGetOptions()).get()

        if (l == null) {
            return false
        }

        return true
    }

    static boolean isValidCountryCode(String countryCode, CountryResource countryResource) {
        if (countryCode == null) {
            throw new IllegalArgumentException('countryCode is null')
        }

        Country country = countryResource.get(new CountryId(countryCode), new CountryGetOptions()).get()
        if (country == null) {
            return false
        }

        return true
    }
}
