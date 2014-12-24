package com.junbo.identity.core.service.filter

import com.junbo.identity.spec.v1.model.UserAttribute
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic

/**
 * Created by xiali_000 on 2014/12/19.
 */
@CompileStatic
class UserAttributeFilter extends ResourceFilterImpl<UserAttribute> {

    @Override
    protected UserAttribute filter(UserAttribute userAttribute, MappingContext context) {
        return selfMapper.filterUserAttribute(userAttribute, context)
    }

    @Override
    protected UserAttribute merge(UserAttribute source, UserAttribute base, MappingContext context) {
        return selfMapper.mergeUserAttribute(source, base, context)
    }
}
