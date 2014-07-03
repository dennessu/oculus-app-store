package com.junbo.identity.data.repository
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.identity.spec.v1.model.EncryptUserPersonalInfo
import com.junbo.sharding.repo.BaseRepository
import groovy.transform.CompileStatic
/**
 * Created by liangfu on 5/14/14.
 */
@CompileStatic
interface EncryptUserPersonalInfoRepository extends BaseRepository<EncryptUserPersonalInfo, UserPersonalInfoId> {
}
