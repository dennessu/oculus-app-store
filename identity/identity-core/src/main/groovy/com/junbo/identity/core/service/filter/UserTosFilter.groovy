package com.junbo.identity.core.service.filter

import com.junbo.common.id.UserTosAgreementId
import com.junbo.identity.spec.v1.model.UserTosAgreement
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
/**
 * Created by liangfu on 3/28/14.
 */
@CompileStatic
class UserTosFilter extends ResourceFilterImpl<UserTosAgreement> {
    @Override
    protected UserTosAgreement filter(UserTosAgreement userTos, MappingContext context) {
        UserTosAgreement result = selfMapper.filterUserTos(userTos, context)
        if (userTos.userId != null) {
            ((UserTosAgreementId)(result.id)).resourcePathPlaceHolder.put('userId', userTos.userId)
        }
        return result
    }

    @Override
    protected UserTosAgreement merge(UserTosAgreement source, UserTosAgreement base, MappingContext context) {
        return selfMapper.mergeUserTos(source, base, context)
    }
}
