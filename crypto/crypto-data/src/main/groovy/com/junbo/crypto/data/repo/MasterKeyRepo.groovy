package com.junbo.crypto.data.repo

import com.junbo.common.id.MasterKeyId
import com.junbo.crypto.spec.model.MasterKey
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import com.junbo.sharding.repo.BaseRepository
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 5/12/14.
 */
@CompileStatic
interface MasterKeyRepo extends BaseRepository<MasterKey, MasterKeyId> {
    @ReadMethod
    Promise<List<MasterKey>> getAllMaterKeys()

    @ReadMethod
    Promise<MasterKey> getMasterKeyByVersion(Integer version)
}
