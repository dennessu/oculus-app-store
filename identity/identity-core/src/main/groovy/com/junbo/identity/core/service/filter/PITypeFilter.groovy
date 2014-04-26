package com.junbo.identity.core.service.filter

import com.junbo.identity.spec.v1.model.PIType
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic

/**
 * Created by haomin on 14-4-25.
 */
@CompileStatic
class PITypeFilter extends ResourceFilterImpl<PIType> {
    @Override
    protected PIType filter(PIType model, MappingContext context) {
        return selfMapper.filterPIType(model, context)
    }

    @Override
    protected PIType merge(PIType source, PIType base, MappingContext context) {
        return selfMapper.mergePIType(source, base, context)
    }
}
