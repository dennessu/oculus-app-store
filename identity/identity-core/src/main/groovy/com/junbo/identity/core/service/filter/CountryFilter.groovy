package com.junbo.identity.core.service.filter

import com.junbo.identity.spec.v1.model.Country
import com.junbo.oom.core.MappingContext

/**
 * Created by haomin on 14-4-25.
 */
class CountryFilter extends ResourceFilterImpl<Country> {
    @Override
    protected Country filter(Country country, MappingContext context) {
        return selfMapper.filterCountry(country, context)
    }

    @Override
    protected Country merge(Country source, Country base, MappingContext context) {
        return selfMapper.mergeCountry(source, base, context)
    }
}
