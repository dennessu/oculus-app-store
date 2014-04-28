package com.junbo.identity.core.service.validator

import com.junbo.common.enumid.CountryId
import com.junbo.identity.spec.v1.model.Country
import com.junbo.identity.spec.v1.option.list.CountryListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by haomin on 14-4-25.
 */
@CompileStatic
public interface CountryValidator {
    Promise<Country> validateForGet(CountryId countryId)
    Promise<Void> validateForSearch(CountryListOptions options)
    Promise<Void> validateForCreate(Country country)
    Promise<Void> validateForUpdate(CountryId countryId, Country country, Country oldCountry)
}