package com.junbo.identity.core.service.filter.pii.impl

import com.junbo.authorization.AuthorizeContext
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.identity.core.service.filter.pii.PIIAdvanceFilter
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 6/30/14.
 */
@CompileStatic
abstract class LBIPIIAdvanceFilter implements PIIAdvanceFilter {

    @Override
    UserPersonalInfo getFilter(UserPersonalInfo userPersonalInfo) {
        return userPersonalInfo
    }

    @Override
    void checkCreatePermission() {
        if (!AuthorizeContext.hasRights('create')) {
            throw AppCommonErrors.INSTANCE.forbidden().exception()
        }
    }

    @Override
    void checkUpdatePermission() {
        if (!AuthorizeContext.hasRights('update')) {
            throw AppCommonErrors.INSTANCE.forbidden().exception()
        }
    }

    @Override
    void checkGetPermission(UserPersonalInfoId piiId) {
        if (!AuthorizeContext.hasRights('get')) {
            throw AppErrors.INSTANCE.userPersonalInfoNotFound(piiId).exception()
        }
    }

    @Override
    void checkDeletePermission() {
        if (!AuthorizeContext.hasRights('delete')) {
            throw AppCommonErrors.INSTANCE.forbidden().exception()
        }
    }
}
