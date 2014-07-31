package com.junbo.identity.core.service.filter

import com.junbo.common.id.UserTFAId
import com.junbo.identity.spec.v1.model.UserTFA
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/24/14.
 */
@CompileStatic
class UserTFAFilter extends ResourceFilterImpl<UserTFA> {

    @Override
    protected UserTFA filter(UserTFA userTFA, MappingContext context) {
        UserTFA result = selfMapper.filterUserTFACode(userTFA, context)
        if (userTFA.userId != null && result.id != null) {
            ((UserTFAId)(result.id)).resourcePathPlaceHolder.put('userId', userTFA.userId)
        }
        return result
    }

    @Override
    protected UserTFA merge(UserTFA source, UserTFA base, MappingContext context) {
        return selfMapper.mergeUserTFACode(source, base, context)
    }
}
