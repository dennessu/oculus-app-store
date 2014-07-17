package com.junbo.crypto.data.repo.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.id.UserCryptoKeyId
import com.junbo.common.id.UserId
import com.junbo.crypto.data.repo.UserCryptoKeyRepo
import com.junbo.crypto.spec.model.UserCryptoKey
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.util.CollectionUtils
/**
 * Created by liangfu on 5/12/14.
 */
@CompileStatic
class CloudantUserCryptoKeyRepoImpl extends CloudantClient<UserCryptoKey> implements UserCryptoKeyRepo {

    @Override
    Promise<List<UserCryptoKey>> getAllUserCryptoKeys(UserId userId) {
        return queryView('by_user_id', userId.value.toString(),
                Integer.MAX_VALUE, 0, false)
    }

    @Override
    Promise<UserCryptoKey> create(UserCryptoKey model) {
        return cloudantPost(model)
    }

    @Override
    Promise<UserCryptoKey> update(UserCryptoKey model, UserCryptoKey oldModel) {
        throw new IllegalArgumentException('Not supported operation')
    }

    @Override
    Promise<UserCryptoKey> get(UserCryptoKeyId id) {
        return cloudantGet(id.toString())
    }

    @Override
    Promise<Void> delete(UserCryptoKeyId id) {
        return cloudantDelete(id.toString())
    }

    @Override
    Promise<UserCryptoKey> getUserCryptoKeyByVersion(UserId userId, Integer version) {
        return queryView('by_user_id_key_version', "${userId.value.toString()}:${version.toString()}",
                Integer.MAX_VALUE, 0, false).then { List<UserCryptoKey> list ->

            if (CollectionUtils.isEmpty(list)) {
                return Promise.pure(null)
            }

            return Promise.pure((UserCryptoKey)list.get(0))
        }
    }
}
