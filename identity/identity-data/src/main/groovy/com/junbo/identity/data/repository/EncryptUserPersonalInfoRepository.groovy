package com.junbo.identity.data.repository

import com.junbo.common.id.EncryptUserPersonalInfoId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.identity.spec.v1.model.EncryptUserPersonalInfo
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.core.annotations.ReadMethod
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 5/14/14.
 */
@CompileStatic
interface EncryptUserPersonalInfoRepository extends
        IdentityBaseRepository<EncryptUserPersonalInfo, EncryptUserPersonalInfoId>  {

    @ReadMethod
    Promise<List<EncryptUserPersonalInfo>> searchByUserPersonalInfoId(UserPersonalInfoId userPersonalInfoId)

    @ReadMethod
    Promise<List<EncryptUserPersonalInfo>> searchByHashValue(String hashSearchInfo)
}
