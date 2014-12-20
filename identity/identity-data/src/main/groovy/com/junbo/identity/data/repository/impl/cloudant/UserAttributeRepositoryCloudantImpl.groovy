package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.id.UserAttributeDefinitionId
import com.junbo.common.id.UserAttributeId
import com.junbo.common.id.UserId
import com.junbo.identity.data.repository.UserAttributeRepository
import com.junbo.identity.spec.v1.model.UserAttribute
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by xiali_000 on 2014/12/19.
 */
@CompileStatic
class UserAttributeRepositoryCloudantImpl extends CloudantClient<UserAttribute> implements UserAttributeRepository {
    @Override
    Promise<UserAttribute> get(UserAttributeId userAttributeId) {
        return super.cloudantGet(userAttributeId.toString())
    }

    @Override
    Promise<UserAttribute> update(UserAttribute userAttribute, UserAttribute oldUserAttribute) {
        return super.cloudantPut(userAttribute, oldUserAttribute)
    }

    @Override
    Promise<UserAttribute> create(UserAttribute userAttribute) {
        return super.cloudantPost(userAttribute)
    }

    @Override
    Promise<Void> delete(UserAttributeId userAttributeId) {
        return super.cloudantDelete(userAttributeId.toString())
    }

    @Override
    Promise<List<UserAttribute>> searchByUserId(UserId userId, Integer limit, Integer offset) {
        return super.queryView('by_user_id', userId.toString(), limit, offset, false)
    }

    @Override
    Promise<List<UserAttribute>> searchByUserAttributeDefinitionId(UserAttributeDefinitionId userAttributeDefinitionId,
                                                                   Integer limit, Integer offset) {
        return super.queryView('by_user_attribute_definition_id', userAttributeDefinitionId.toString(),
            limit, offset, false)
    }

    @Override
    Promise<List<UserAttribute>> searchByUserIdAndAttributeDefinitionId(UserId userId, UserAttributeDefinitionId userAttributeDefinitionId, Integer limit, Integer offset) {
        def startKey = [userId.toString(), userAttributeDefinitionId.toString()]
        def endKey = [userId.toString(), userAttributeDefinitionId.toString()]
        return queryView('by_user_id_attribute_definition_id', startKey.toArray(new String()), endKey.toArray(new String()),
                false, limit, offset, false)
    }
}
