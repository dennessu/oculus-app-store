package com.junbo.identity.core.service.filter

import com.junbo.authorization.filter.AbstractResourceFilter
import com.junbo.identity.spec.v1.model.User
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by kg on 3/19/2014.
 */
@CompileStatic
class UserFilter extends AbstractResourceFilter<User> {
    @Autowired
    protected SelfMapper selfMapper

    @Override
    protected User filter(User user, MappingContext context) {
        return selfMapper.filterUser(user, context)
    }

    @Override
    protected User merge(User source, User base, MappingContext context) {
        return selfMapper.mergeUser(source, base, context)
    }
}
