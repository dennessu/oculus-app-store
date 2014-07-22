package com.junbo.order.mock

import com.junbo.common.enumid.CountryId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.Country
import com.junbo.identity.spec.v1.option.list.CountryListOptions
import com.junbo.identity.spec.v1.option.model.CountryGetOptions
import com.junbo.identity.spec.v1.resource.CountryResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

/**
 * Mock implementation of country resource.
 */
@CompileStatic
@Component('mockCountryResource')
class MockCountryResource extends BaseMock implements CountryResource {
    @Override
    Promise<Country> create(Country country) {
        return null
    }

    @Override
    Promise<Country> put(CountryId countryId, Country country) {
        return null
    }

    @Override
    Promise<Country> patch(CountryId countryId, Country country) {
        return null
    }

    @Override
    Promise<Country> get(CountryId countryId,CountryGetOptions getOptions) {
        Country country = new Country()
        country.setId(countryId)
        country.setCountryCode('US')
        return Promise.pure(country)
    }

    @Override
    Promise<Results<Country>> list(CountryListOptions listOptions) {
        return null
    }

    @Override
    Promise<Void> delete(CountryId countryId) {
        return null
    }
}
