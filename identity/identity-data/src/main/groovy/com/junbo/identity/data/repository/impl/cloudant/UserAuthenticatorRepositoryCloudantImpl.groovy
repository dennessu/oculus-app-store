package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.id.UserAuthenticatorId
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.identity.data.repository.UserAuthenticatorRepository
import com.junbo.identity.spec.v1.model.UserAuthenticator
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
/**
 * Created by haomin on 14-4-10.
 */
@CompileStatic
class UserAuthenticatorRepositoryCloudantImpl extends CloudantClient<UserAuthenticator>
        implements UserAuthenticatorRepository{

    @Override
    Promise<UserAuthenticator> create(UserAuthenticator authenticator) {
        return cloudantPost(authenticator)
    }

    @Override
    Promise<UserAuthenticator> update(UserAuthenticator authenticator, UserAuthenticator oldAuthenticator) {
        return cloudantPut(authenticator, oldAuthenticator)
    }

    @Override
    Promise<UserAuthenticator> get(UserAuthenticatorId authenticatorId) {
        return cloudantGet(authenticatorId.toString())
    }

    @Override
    Promise<Void> delete(UserAuthenticatorId authenticatorId) {
        return cloudantDelete(authenticatorId.toString())
    }

    @Override
    Promise<Results<UserAuthenticator>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return queryViewResults('by_user_id', userId.toString(), limit, offset, false)
    }

    @Override
    Promise<Results<UserAuthenticator>> searchByUserIdAndType(UserId userId, String type, Integer limit, Integer offset) {
        def startKey = [userId.toString(), type]
        def endKey = [userId.toString(), type]
        return queryViewResults('by_user_id_auth_type', startKey.toArray(new String()), endKey.toArray(new String()),
                false, limit, offset, false)
    }

    @Override
    Promise<Results<UserAuthenticator>> searchByExternalId(String externalId, Integer limit, Integer offset) {
        return queryViewResults('by_authenticator_externalId', externalId, limit, offset, false)
    }

    @Override
    Promise<Results<UserAuthenticator>> searchByUserIdAndTypeAndExternalId(UserId userId, String type, String externalId,
                                                                        Integer limit, Integer offset) {
        def startKey = [userId.toString(), type, externalId]
        def endKey = [userId.toString(), type, externalId]
        return queryViewResults('by_user_id_auth_type_externalId', startKey.toArray(new String()), endKey.toArray(new String()), false, limit,
                offset, false)
    }

    @Override
    Promise<Results<UserAuthenticator>> searchByUserIdAndExternalId(UserId userId, String externalId, Integer limit,
                                                                 Integer offset) {
        def startKey = [userId.toString(), externalId]
        def endKey = [userId.toString(), externalId]
        return queryViewResults('by_user_id_externalId', startKey.toArray(new String()), endKey.toArray(new String()),
                false, limit, offset, false)
    }

    @Override
    Promise<Results<UserAuthenticator>> searchByExternalIdAndType(String externalId, String type, Integer limit,
                                                               Integer offset) {
        def startKey = [externalId, type]
        def endKey = [externalId, type]
        return queryViewResults('by_authenticator_externalId_auth_type', startKey.toArray(new String()),
                endKey.toArray(new String()), false, limit, offset, false)
    }
}
