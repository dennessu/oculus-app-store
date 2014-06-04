package com.junbo.billing.clientproxy.impl

import com.junbo.billing.clientproxy.CountryFacade
import com.junbo.common.enumid.CountryId
import com.junbo.identity.spec.v1.model.Country
import com.junbo.identity.spec.v1.option.model.CountryGetOptions
import com.junbo.identity.spec.v1.resource.CountryResource
import com.junbo.langur.core.promise.Promise

import javax.annotation.Resource

/**
 * Created by xmchen on 14-6-4.
 */
class CountryFacadeImpl implements CountryFacade {

    @Resource(name = 'billingCountryClient')
    private CountryResource countryResource

    @Override
    Promise<Country> getCountry(String countryId) {
        return countryResource.get(new CountryId(countryId), new CountryGetOptions())
    }
}
