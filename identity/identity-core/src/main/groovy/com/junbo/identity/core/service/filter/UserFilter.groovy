package com.junbo.identity.core.service.filter
import com.junbo.identity.spec.model.users.User
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
/**
 * Created by kg on 3/19/2014.
 */
@CompileStatic
class UserFilter extends ResourceFilterImpl<User> {
    @Override
    protected User filter(User user, MappingContext context) {
        return selfMapper.filterUser(user, context)
    }

    @Override
    protected User merge(User source, User base, MappingContext context) {
        return selfMapper.mergeUser(source, base, context)
    }
}
