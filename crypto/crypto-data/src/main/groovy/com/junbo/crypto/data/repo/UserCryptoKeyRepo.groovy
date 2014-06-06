package com.junbo.crypto.data.repo

import com.junbo.common.id.UserCryptoKeyId
import com.junbo.common.id.UserId
import com.junbo.crypto.spec.model.UserCryptoKey
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import com.junbo.sharding.repo.BaseRepository
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 5/12/14.
 */
@CompileStatic
interface UserCryptoKeyRepo extends BaseRepository<UserCryptoKey, UserCryptoKeyId> {

    @ReadMethod
    Promise<List<UserCryptoKey>> getAllUserCryptoKeys(UserId userId)

    @ReadMethod
    Promise<UserCryptoKey> getUserCryptoKeyByVersion(UserId userId, Integer version)
}
