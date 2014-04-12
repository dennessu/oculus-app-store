package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.UserAuthenticatorId
import com.junbo.identity.data.repository.UserAuthenticatorRepository
import com.junbo.identity.spec.v1.model.UserAuthenticator
import com.junbo.identity.spec.v1.option.list.AuthenticatorListOptions
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
        authenticator.id = new UserAuthenticatorId(idGenerator.nextId(authenticator.userId.value))
        super.cloudantPost(authenticator)
        return get((UserAuthenticatorId)authenticator.id)
    }

    @Override
    Promise<UserAuthenticator> update(UserAuthenticator authenticator) {
        super.cloudantPut(authenticator)
        return get((UserAuthenticatorId)authenticator.id)
    }

    @Override
    Promise<UserAuthenticator> get(UserAuthenticatorId authenticatorId) {
        return Promise.pure((UserAuthenticator)super.cloudantGet(authenticatorId.toString()))
    }

    @Override
    Promise<Void> delete(UserAuthenticatorId authenticatorId) {
        super.cloudantDelete(authenticatorId.value)
        return Promise.pure(null)
    }

    @Override
    Promise<List<UserAuthenticator>> search(AuthenticatorListOptions getOption) {
        def list = []
        if (getOption.userId != null && getOption.userId.value != null) {
            if (getOption.type != null && getOption.value != null) {
                list = super.queryView('by_user_id_auth_type_auth_value',
                        "${getOption.userId.value}:${getOption.type}:${getOption.value}",
                        getOption.limit, getOption.offset, false)
            }
            else if (getOption.type != null) {
                list = super.queryView('by_user_id_auth_type', "${getOption.userId.value}:${getOption.type}",
                        getOption.limit, getOption.offset, false)
            }
            else if (getOption.value != null) {
                list = super.queryView('by_user_id_auth_value', "${getOption.userId.value}:${getOption.value}",
                        getOption.limit, getOption.offset, false)
            }
            else {
                list = super.queryView('by_user_id', "${getOption.userId.value}",
                        getOption.limit, getOption.offset, false)
            }
        }
        else if (getOption != null && getOption.value != null) {
            list = super.queryView('by_authenticator_value', getOption.value,
                    getOption.limit, getOption.offset, false)
        }

        return list.size() > 0 ? Promise.pure(list) : Promise.pure(null)
    }

    protected CloudantViews views = new CloudantViews(
            views: [
                    'by_authenticator_value': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.value, doc._id)' +
                                    '}',
                            resultClass: String),
                    'by_user_id_auth_type': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.user.value + \':\' + doc.type, doc._id)' +
                                    '}',
                            resultClass: String),
                    'by_user_id_auth_value': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.user.value + \':\' + doc.value, doc._id)' +
                                    '}',
                            resultClass: String),
                    'by_user_id_auth_type_auth_value': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.user.value + \':\' + doc.type + \':\' + doc.value, doc._id)' +
                                    '}',
                            resultClass: String),
                    'by_user_id': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.user.value, doc._id)' +
                                    '}',
                            resultClass: String)
            ]
    )

    @Override
    protected CloudantViews getCloudantViews() {
        return views
    }
}
