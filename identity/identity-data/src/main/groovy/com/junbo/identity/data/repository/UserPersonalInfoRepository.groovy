package com.junbo.identity.data.repository

import com.junbo.common.id.UserPersonalInfoId
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/25/14.
 */
@CompileStatic
interface UserPersonalInfoRepository extends IdentityBaseRepository<UserPersonalInfo, UserPersonalInfoId> {
}
