package com.junbo.crypto.data.repo.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.UserCryptoKeyId
import com.junbo.common.id.UserId
import com.junbo.crypto.data.repo.UserCryptoKeyRepo
import com.junbo.crypto.spec.model.UserCryptoKey
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 5/12/14.
 */
@CompileStatic
class CloudantUserCryptoKeyRepoImpl extends CloudantClient<UserCryptoKey> implements UserCryptoKeyRepo {
    private IdGenerator idGenerator

    @Override
    protected CloudantViews getCloudantViews() {
        return views
    }

    @Override
    Promise<List<UserCryptoKey>> searchAllUserCryptoKeys(UserId userId) {
        def list = super.queryView('by_user_id', userId.value.toString(),
                Integer.MAX_VALUE, 0, false)
        return Promise.pure(list)
    }

    @Override
    Promise<UserCryptoKey> create(UserCryptoKey model) {
        if (model.id == null) {
            model.id = new UserCryptoKeyId(idGenerator.nextId(model.userId.value))
        }
        return Promise.pure((UserCryptoKey)super.cloudantPost(model))
    }

    @Override
    Promise<UserCryptoKey> update(UserCryptoKey model) {
        throw new IllegalArgumentException('Not supported operation')
    }

    @Override
    Promise<UserCryptoKey> get(UserCryptoKeyId id) {
        return Promise.pure((UserCryptoKey)super.cloudantGet(id.toString()))
    }

    @Override
    Promise<Void> delete(UserCryptoKeyId id) {
        super.cloudantDelete(id.toString())
        return Promise.pure(null)
    }

    protected CloudantViews views = new CloudantViews(
            views: [
                    'by_user_id': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.userId.value.toString(), doc._id)' +
                                    '}',
                            resultClass: String)
            ]
    )

    @Required
    void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator
    }
}
