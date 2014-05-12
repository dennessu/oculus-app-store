package com.junbo.crypto.data.repo

import com.junbo.common.id.UserCryptoKeyId
import com.junbo.common.id.UserId
import com.junbo.crypto.spec.model.UserCryptoKey
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 5/12/14.
 */
@CompileStatic
interface UserCryptoKeyRepo extends CryptoBaseRepository<UserCryptoKey, UserCryptoKeyId> {
    Promise<List<UserCryptoKey>> searchAllUserCryptoKeys(UserId userId)
}
