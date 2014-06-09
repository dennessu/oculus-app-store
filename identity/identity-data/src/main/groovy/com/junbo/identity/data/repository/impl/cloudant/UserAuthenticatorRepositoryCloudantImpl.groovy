package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.UserAuthenticatorId
import com.junbo.common.id.UserId
import com.junbo.identity.data.repository.UserAuthenticatorRepository
import com.junbo.identity.spec.v1.model.UserAuthenticator
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.ShardAlgorithm
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
/**
 * Created by haomin on 14-4-10.
 */
@CompileStatic
class UserAuthenticatorRepositoryCloudantImpl extends CloudantClient<UserAuthenticator>
        implements UserAuthenticatorRepository{
    private ShardAlgorithm shardAlgorithm
    private IdGenerator idGenerator

    @Required
    void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator
    }

    @Required
    void setShardAlgorithm(ShardAlgorithm shardAlgorithm) {
        this.shardAlgorithm = shardAlgorithm
    }

    @Override
    Promise<UserAuthenticator> create(UserAuthenticator authenticator) {
        if (authenticator.id == null) {
            authenticator.id = new UserAuthenticatorId(idGenerator.nextId(authenticator.userId.value))
        }
        return super.cloudantPost(authenticator)
    }

    @Override
    Promise<UserAuthenticator> update(UserAuthenticator authenticator) {
        return super.cloudantPut(authenticator)
    }

    @Override
    Promise<UserAuthenticator> get(UserAuthenticatorId authenticatorId) {
        return super.cloudantGet(authenticatorId.toString())
    }

    @Override
    Promise<Void> delete(UserAuthenticatorId authenticatorId) {
        return super.cloudantDelete(authenticatorId.toString())
    }

    @Override
    Promise<List<UserAuthenticator>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return super.queryView('by_user_id', userId.toString(), limit, offset, false)
    }

    @Override
    Promise<List<UserAuthenticator>> searchByUserIdAndType(UserId userId, String type, Integer limit, Integer offset) {
        return super.queryView('by_user_id_auth_type', "${userId.toString()}:${type}", limit, offset, false)
    }

    @Override
    Promise<List<UserAuthenticator>> searchByExternalId(String externalId, Integer limit, Integer offset) {
        return super.queryView('by_authenticator_externalId', externalId, limit, offset, false)
    }

    @Override
    Promise<List<UserAuthenticator>> searchByUserIdAndTypeAndExternalId(UserId userId, String type, String externalId,
                                                                        Integer limit, Integer offset) {
        return super.queryView('by_user_id_auth_type_externalId', "${userId.value}:${type}:${externalId}", limit,
                offset, false)
    }

    @Override
    Promise<List<UserAuthenticator>> searchByUserIdAndExternalId(UserId userId, String externalId, Integer limit,
                                                                 Integer offset) {
        return super.queryView('by_user_id_externalId', "${userId.toString()}:${externalId}", limit, offset, false)
    }

    @Override
    Promise<List<UserAuthenticator>> searchByExternalIdAndType(String externalId, String type, Integer limit,
                                                               Integer offset) {
        return super.queryView('by_authenticator_externalId_auth_type', "${externalId}:${type}", limit, offset,
                false)
    }
    protected CloudantViews views = new CloudantViews(
            views: [
                    'by_authenticator_externalId': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.externalId, doc._id)' +
                                    '}',
                            resultClass: String),
                    'by_user_id_auth_type': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.userId + \':\' + doc.type, doc._id)' +
                                    '}',
                            resultClass: String),
                    'by_user_id_externalId': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.userId + \':\' + doc.externalId, doc._id)' +
                                    '}',
                            resultClass: String),
                    'by_user_id_auth_type_externalId': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.userId + \':\' + doc.type + \':\' + doc.externalId, doc._id)' +
                                    '}',
                            resultClass: String),
                    'by_user_id': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.userId, doc._id)' +
                                    '}',
                            resultClass: String),
                    'by_authenticator_externalId_auth_type': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    ' emit(doc.externalId + \':\' + doc.type, doc._id)' +
                                    '}',
                            resultClass: String)
            ]
    )

    @Override
    protected CloudantViews getCloudantViews() {
        return views
    }
}
