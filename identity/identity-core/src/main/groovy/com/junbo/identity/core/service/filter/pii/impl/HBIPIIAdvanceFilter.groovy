package com.junbo.identity.core.service.filter.pii.impl

import com.junbo.authorization.AuthorizeContext
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.identity.core.service.filter.pii.PIIAdvanceFilter
import com.junbo.identity.spec.error.AppErrors
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 6/30/14.
 */
@CompileStatic
abstract class HBIPIIAdvanceFilter implements PIIAdvanceFilter {

    @Override
    void checkCreatePermission() {
        if (!AuthorizeContext.hasRights('pii.create')) {
            throw AppErrors.INSTANCE.invalidAccess().exception()
        }
    }

    @Override
    void checkUpdatePermission() {
        if (!AuthorizeContext.hasRights('pii.update')) {
            throw AppErrors.INSTANCE.invalidAccess().exception()
        }
    }

    @Override
    void checkGetPermission(UserPersonalInfoId piiId) {
        if (!AuthorizeContext.hasRights('read') && !AuthorizeContext.hasRights('pii.read') ) {
            throw AppErrors.INSTANCE.userPersonalInfoNotFound(piiId).exception()
        }
    }

    @Override
    void checkDeletePermission() {
        if (!AuthorizeContext.hasRights('pii.delete')) {
            throw AppErrors.INSTANCE.invalidAccess().exception()
        }
    }
}
