package com.junbo.identity.core.service.filter

import com.junbo.identity.spec.v1.model.Communication
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic

/**
 * Created by haomin on 14-4-25.
 */
@CompileStatic
class CommunicationFilter extends ResourceFilterImpl<Communication> {
    @Override
    protected Communication filter(Communication model, MappingContext context) {
        return selfMapper.filterCommunication(model, context)
    }

    @Override
    protected Communication merge(Communication source, Communication base, MappingContext context) {
        return selfMapper.mergeCommunication(source, base, context)
    }
}
