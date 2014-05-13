package com.junbo.crypto.data.repo

import com.junbo.common.id.MasterKeyId
import com.junbo.crypto.spec.model.MasterKey
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 5/12/14.
 */
@CompileStatic
interface MasterKeyRepo extends CryptoBaseRepository<MasterKey, MasterKeyId> {
    Promise<List<MasterKey>> getAllMaterKeys()
}
