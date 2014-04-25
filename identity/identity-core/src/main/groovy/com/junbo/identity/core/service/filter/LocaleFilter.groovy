package com.junbo.identity.core.service.filter

import com.junbo.oom.core.MappingContext

/**
 * Created by haomin on 14-4-25.
 */
class LocaleFilter extends ResourceFilterImpl<com.junbo.identity.spec.v1.model.Locale> {
    @Override
    protected com.junbo.identity.spec.v1.model.Locale filter(com.junbo.identity.spec.v1.model.Locale model, MappingContext context) {
        return selfMapper.filterLocale(model, context)
    }

    @Override
    protected com.junbo.identity.spec.v1.model.Locale merge(com.junbo.identity.spec.v1.model.Locale source, com.junbo.identity.spec.v1.model.Locale base, MappingContext context) {
        return selfMapper.filterLocale(source, base, context)
    }
}
