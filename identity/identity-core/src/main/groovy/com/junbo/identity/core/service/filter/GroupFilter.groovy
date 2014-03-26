package com.junbo.identity.core.service.filter

import com.junbo.identity.spec.model.users.Group
import com.junbo.oom.core.MappingContext

/**
 * Created by liangfu on 3/26/14.
 */
class GroupFilter extends ResourceFilterImpl<Group> {
    @Override
    protected Group filter(Group group, MappingContext context) {
        return selfMapper.filterGroup(group, context)
    }

    @Override
    protected Group merge(Group source, Group base, MappingContext context) {
        return selfMapper.mergeGroup(source, base, context)
    }
}
