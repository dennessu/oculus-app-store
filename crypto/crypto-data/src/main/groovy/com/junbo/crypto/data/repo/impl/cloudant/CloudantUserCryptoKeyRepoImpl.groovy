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
import org.springframework.util.CollectionUtils

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
    Promise<List<UserCryptoKey>> getAllUserCryptoKeys(UserId userId) {
        return super.queryView('by_user_id', userId.value.toString(),
                Integer.MAX_VALUE, 0, false)
    }

    @Override
    Promise<UserCryptoKey> create(UserCryptoKey model) {
        if (model.id == null) {
            model.id = new UserCryptoKeyId(idGenerator.nextId(model.userId.value))
        }
        return super.cloudantPost(model)
    }

    @Override
    Promise<UserCryptoKey> update(UserCryptoKey model) {
        throw new IllegalArgumentException('Not supported operation')
    }

    @Override
    Promise<UserCryptoKey> get(UserCryptoKeyId id) {
        return super.cloudantGet(id.toString())
    }

    @Override
    Promise<Void> delete(UserCryptoKeyId id) {
        return super.cloudantDelete(id.toString())
    }

    @Override
    Promise<UserCryptoKey> getUserCryptoKeyByVersion(UserId userId, Integer version) {
        return super.queryView('by_user_id_key_version', "${userId.value.toString()}:${version.toString()}",
                Integer.MAX_VALUE, 0, false).then { List<UserCryptoKey> list ->

            if (CollectionUtils.isEmpty(list)) {
                return Promise.pure(null)
            }

            return Promise.pure((UserCryptoKey)list.get(0))
        }
    }

    protected CloudantViews views = new CloudantViews(
            views: [
                    'by_user_id': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.userId, doc._id)' +
                                    '}',
                            resultClass: String),
                    'by_user_id_key_version': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.userId + \':\' + doc.keyVersion.toString(), doc._id)' +
                                    '}',
                            resultClass: String)
            ]
    )

    @Required
    void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator
    }
}
