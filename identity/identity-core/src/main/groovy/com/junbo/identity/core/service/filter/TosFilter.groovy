package com.junbo.identity.core.service.filter

import com.junbo.identity.spec.v1.model.Tos
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/9/14.
 */
@CompileStatic
class TosFilter extends ResourceFilterImpl<Tos> {
    @Override
    protected Tos filter(Tos tos, MappingContext context) {
        return selfMapper.filterTos(tos, context)
    }

    @Override
    protected Tos merge(Tos source, Tos base, MappingContext context) {
        return selfMapper.mergeTos(source, base, context)
    }
}
