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
        return selfMapper.filterUserTos(userTos, context)
    }

    @Override
    protected UserTosAgreement merge(UserTosAgreement source, UserTosAgreement base, MappingContext context) {
        return selfMapper.mergeUserTos(source, base, context)
    }
}
