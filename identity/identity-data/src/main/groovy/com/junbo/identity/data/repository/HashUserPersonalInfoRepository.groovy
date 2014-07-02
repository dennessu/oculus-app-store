package com.junbo.identity.data.repository

import com.junbo.common.id.UserPersonalInfoId
import com.junbo.identity.spec.v1.model.HashUserPersonalInfo
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import com.junbo.sharding.repo.BaseRepository
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 5/14/14.
 */
@CompileStatic
interface HashUserPersonalInfoRepository extends BaseRepository<HashUserPersonalInfo, UserPersonalInfoId> {
    @ReadMethod
    Promise<List<UserPersonalInfoId>> searchByHashValue(String hashSearchInfo)
}
