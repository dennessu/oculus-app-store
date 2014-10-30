package com.junbo.identity.service

import com.junbo.common.id.CommunicationId
import com.junbo.common.id.UserCommunicationId
import com.junbo.common.id.UserId
import com.junbo.identity.spec.v1.model.UserCommunication
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 10/21/14.
 */
@CompileStatic
public interface UserCommunicationService {
    Promise<UserCommunication> get(UserCommunicationId id)

    Promise<UserCommunication> create(UserCommunication model)

    Promise<UserCommunication> update(UserCommunication model, UserCommunication oldModel)

    Promise<Void> delete(UserCommunicationId id)

    Promise<List<UserCommunication>> searchByUserId(UserId userId, Integer limit, Integer offset)

    Promise<List<UserCommunication>> searchByCommunicationId(CommunicationId communicationId, Integer limit,
                                                             Integer offset)

    Promise<List<UserCommunication>> searchByUserIdAndCommunicationId(UserId userId, CommunicationId communicationId,
                                                                      Integer limit, Integer offset)
}