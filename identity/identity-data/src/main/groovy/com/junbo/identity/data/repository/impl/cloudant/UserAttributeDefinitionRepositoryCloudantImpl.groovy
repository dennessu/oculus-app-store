package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.id.UserAttributeDefinitionId
import com.junbo.identity.data.repository.UserAttributeDefinitionRepository
import com.junbo.identity.spec.v1.model.UserAttributeDefinition
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by xiali_000 on 2014/12/19.
 */
@CompileStatic
class UserAttributeDefinitionRepositoryCloudantImpl
        extends CloudantClient<UserAttributeDefinition> implements UserAttributeDefinitionRepository {

    @Override
    Promise<UserAttributeDefinition> get(UserAttributeDefinitionId userAttributeDefinitionId) {
        return super.cloudantGet(userAttributeDefinitionId.toString())
    }

    @Override
    Promise<UserAttributeDefinition> create(UserAttributeDefinition userAttributeDefinition) {
        return super.cloudantPost(userAttributeDefinition)
    }

    @Override
    Promise<UserAttributeDefinition> update(UserAttributeDefinition newUserAttributeDefinition,
                                            UserAttributeDefinition oldUserAttributeDefinition) {
        return super.cloudantPut(newUserAttributeDefinition, oldUserAttributeDefinition)
    }

    @Override
    Promise<Void> delete(UserAttributeDefinitionId userAttributeDefinitionId) {
        return super.cloudantDelete(userAttributeDefinitionId.toString())
    }

    @Override
    Promise<List<UserAttributeDefinition>> getAll(Integer limit, Integer offset) {
        return super.cloudantGetAll(limit, offset, false)
    }
}
