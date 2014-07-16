package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.id.CommunicationId
import com.junbo.common.id.UserCommunicationId
import com.junbo.common.id.UserId
import com.junbo.identity.data.repository.UserCommunicationRepository
import com.junbo.identity.spec.v1.model.UserCommunication
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
/**
 * Created by haomin on 14-4-11.
 */
@CompileStatic
class UserCommunicationRepositoryCloudantImpl extends CloudantClient<UserCommunication> implements UserCommunicationRepository {

    @Override
    Promise<UserCommunication> create(UserCommunication entity) {
        return cloudantPost(entity)
    }

    @Override
    Promise<UserCommunication> update(UserCommunication entity, UserCommunication oldEntity) {
        return cloudantPut(entity, oldEntity)
    }

    @Override
    Promise<UserCommunication> get(UserCommunicationId id) {
        return cloudantGet(id.toString())
    }

    @Override
    Promise<List<UserCommunication>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return queryView('by_user_id', userId.toString(), limit, offset, false)
    }

    @Override
    Promise<List<UserCommunication>> searchByCommunicationId(CommunicationId communicationId, Integer limit,
                                                             Integer offset) {
        return queryView('by_communication_id', communicationId.toString(), limit, offset, false)
    }

    @Override
    Promise<List<UserCommunication>> searchByUserIdAndCommunicationId(UserId userId, CommunicationId communicationId,
                                                                      Integer limit, Integer offset) {
        def startKey = [userId.toString(), communicationId.toString()]
        def endKey = [userId.toString(), communicationId.toString()]
        return queryView('by_user_id_communication_id', startKey.toArray(new String()), endKey.toArray(new String()), false, limit, offset, false)
    }

    @Override
    Promise<Void> delete(UserCommunicationId id) {
        return cloudantDelete(id.toString())
    }
}
