package com.junbo.identity.core.service.filter

import com.junbo.identity.spec.v1.model.UserAttributeDefinition
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic

/**
 * Created by xiali_000 on 2014/12/19.
 */
@CompileStatic
class UserAttributeDefinitionFilter extends ResourceFilterImpl<UserAttributeDefinition> {

    @Override
    protected UserAttributeDefinition filter(UserAttributeDefinition userAttributeDefinition, MappingContext context) {
        return selfMapper.filterUserAttributeDefinition(userAttributeDefinition, context)
    }

    @Override
    protected UserAttributeDefinition merge(UserAttributeDefinition source, UserAttributeDefinition base, MappingContext context) {
        return selfMapper.mergeUserAttributeDefinition(source, base, context)
    }
}
