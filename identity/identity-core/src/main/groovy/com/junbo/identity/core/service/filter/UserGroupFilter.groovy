package com.junbo.identity.core.service.filter

import com.junbo.identity.spec.v1.model.UserGroup
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/28/14.
 */
@CompileStatic
class UserGroupFilter extends ResourceFilterImpl<UserGroup> {
    @Override
    protected UserGroup filter(UserGroup userGroup, MappingContext context) {
        return selfMapper.filterUserGroup(userGroup, context)
    }

    @Override
    protected UserGroup merge(UserGroup source, UserGroup base, MappingContext context) {
        return selfMapper.mergeUserGroup(source, base, context)
    }
}
