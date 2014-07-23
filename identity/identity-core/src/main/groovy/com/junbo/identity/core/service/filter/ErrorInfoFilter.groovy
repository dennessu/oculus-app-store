package com.junbo.identity.core.service.filter

import com.junbo.identity.spec.v1.model.ErrorInfo
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by liangfu on 7/23/14.
 */
@CompileStatic
class ErrorInfoFilter extends ResourceFilterImpl<ErrorInfo> {
    @Autowired
    protected SelfMapper selfMapper

    @Override
    protected ErrorInfo filter(ErrorInfo model, MappingContext context) {
        return selfMapper.filterErrorInfo(model, context)
    }

    @Override
    protected ErrorInfo merge(ErrorInfo source, ErrorInfo base, MappingContext context) {
        return selfMapper.mergeErrorInfo(source, base, context)
    }
}
