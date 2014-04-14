package com.junbo.identity.core.service.filter

import com.junbo.identity.spec.v1.model.UserSecurityQuestion
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 3/31/14.
 */
@CompileStatic
class UserSecurityQuestionFilter extends ResourceFilterImpl<UserSecurityQuestion> {

    @Override
    protected UserSecurityQuestion filter(UserSecurityQuestion user, MappingContext context) {
        return selfMapper.filterUserSecurityQuestion(user, context)
    }

    @Override
    protected UserSecurityQuestion merge(UserSecurityQuestion source,
                                         UserSecurityQuestion base, MappingContext context) {
        return selfMapper.mergeUserSecurityQuestion(source, base, context)
    }
}
