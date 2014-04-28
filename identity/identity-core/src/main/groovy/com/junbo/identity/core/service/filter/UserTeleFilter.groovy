package com.junbo.identity.core.service.filter

import com.junbo.common.id.UserTeleId
import com.junbo.identity.spec.v1.model.UserTeleCode
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/24/14.
 */
@CompileStatic
class UserTeleFilter extends ResourceFilterImpl<UserTeleCode> {

    @Override
    protected UserTeleCode filter(UserTeleCode userTele, MappingContext context) {
        UserTeleCode result = selfMapper.filterUserTeleCode(userTele, context)
        if (userTele.userId != null) {
            ((UserTeleId)(result.id)).resourcePathPlaceHolder.put('userId', userTele.userId)
        }
        return result
    }

    @Override
    protected UserTeleCode merge(UserTeleCode source, UserTeleCode base, MappingContext context) {
        return selfMapper.mergeUserTeleCode(source, base, context)
    }
}
