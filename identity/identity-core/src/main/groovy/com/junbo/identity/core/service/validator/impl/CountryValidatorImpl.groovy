package com.junbo.identity.core.service.validator.impl

import com.junbo.common.enumid.CountryId
import com.junbo.identity.core.service.validator.CountryValidator
import com.junbo.identity.spec.v1.model.Country
import com.junbo.identity.spec.v1.option.list.CountryListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by haomin on 14-4-25.
 */
@CompileStatic
class CountryValidatorImpl implements CountryValidator {
    @Override
    Promise<Country> validateForGet(CountryId countryId) {
        return null
    }

    @Override
    Promise<Void> validateForSearch(CountryListOptions options) {
        return null
    }

    @Override
    Promise<Void> validateForCreate(Country country) {
        return null
    }

    @Override
    Promise<Void> validateForUpdate(CountryId countryId, Country country, Country oldCountry) {
        return null
    }
}
