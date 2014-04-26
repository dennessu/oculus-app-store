package com.junbo.identity.core.service.filter

import com.junbo.identity.spec.v1.model.UserCommunication
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
/**
 * Created by liangfu on 3/31/14.
 */
@CompileStatic
class UserCommunicationFilter extends ResourceFilterImpl<UserCommunication> {
    @Override
    protected UserCommunication filter(UserCommunication userOptin, MappingContext context) {
        return selfMapper.filterUserCommunication(userOptin, context)
    }

    @Override
    protected UserCommunication merge(UserCommunication source, UserCommunication base, MappingContext context) {
        return selfMapper.mergeUserCommunication(source, base, context)
    }
}
