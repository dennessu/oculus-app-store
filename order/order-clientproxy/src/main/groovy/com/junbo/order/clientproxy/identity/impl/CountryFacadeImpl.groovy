package com.junbo.order.clientproxy.identity.impl

import com.junbo.common.enumid.CountryId
import com.junbo.common.error.AppCommonErrors
import com.junbo.identity.spec.v1.model.Country
import com.junbo.identity.spec.v1.option.model.CountryGetOptions
import com.junbo.identity.spec.v1.resource.CountryResource
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.identity.CountryFacade
import com.junbo.order.spec.error.AppErrors
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 * Implementation of country facade.
 */
@Component('orderCountryFacade')
@CompileStatic
@TypeChecked
class CountryFacadeImpl implements CountryFacade {
    private static final Logger LOGGER = LoggerFactory.getLogger(CountryFacadeImpl)

    @Resource(name = 'order.identityCountryClient')
    CountryResource countryResource

    @Override
    Promise<Country> getCountry(String country) {
        if (country == null || country.isEmpty()) {
            throw AppCommonErrors.INSTANCE.parameterRequired('country').exception()
        }
        return countryResource.get(new CountryId(country), new CountryGetOptions()).recover {
            Throwable throwable ->
                LOGGER.error('name=error_in_get_country: ' + country, throwable)
                throw AppErrors.INSTANCE.countryNotValid(country).exception()
        }.then { com.junbo.identity.spec.v1.model.Country c ->
            if(c == null) {
                LOGGER.error('name=country_is_null: ' + country)
                throw AppErrors.INSTANCE.countryNotValid(country).exception()
            }
            return Promise.pure(c)
        }
    }
}
