package com.junbo.identity.core.service.filter

import com.junbo.oom.core.MappingContext

/**
 * Created by haomin on 14-4-25.
 */
class CurrencyFilter extends ResourceFilterImpl<com.junbo.identity.spec.v1.model.Currency> {
    @Override
    protected com.junbo.identity.spec.v1.model.Currency filter(com.junbo.identity.spec.v1.model.Currency currency, MappingContext context) {
        return selfMapper.filterCurrency(currency, context)
    }

    @Override
    protected com.junbo.identity.spec.v1.model.Currency merge(com.junbo.identity.spec.v1.model.Currency source, com.junbo.identity.spec.v1.model.Currency base, MappingContext context) {
        return selfMapper.mergeCurrency(source, base, context)
    }
}
