package com.junbo.identity.core.service.filter.pii.impl

import com.junbo.authorization.AuthorizeContext
import com.junbo.identity.core.service.filter.pii.PIIAdvanceFilter
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import groovy.transform.CompileStatic

/**
 * This is LBI data, so just has create/update/get permission is enough
 * Created by liangfu on 6/30/14.
 */
@CompileStatic
class WipedAdvanceFilter extends LBIPIIAdvanceFilter {
    @Override
    boolean handles(UserPersonalInfo userPersonalInfo) {
        return userPersonalInfo.type == UserPersonalInfoType.WIPED.toString()
    }
}
