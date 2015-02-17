package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViewQueryOptions
import com.junbo.common.id.OrganizationId
import com.junbo.common.id.UserAttributeDefinitionId
import com.junbo.common.model.Results
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
        return cloudantGetAll(new CloudantViewQueryOptions(
                limit: limit,
                skip: offset
        )).then { Results<UserAttributeDefinition> results ->
            return Promise.pure(results.items)
        }
    }

    @Override
    Promise<List<UserAttributeDefinition>> getByOrganizationId(OrganizationId organizationId, Integer limit, Integer offset) {
        return super.queryView('by_organization_id', organizationId.toString(), limit, offset, false)
    }

    @Override
    Promise<List<UserAttributeDefinition>> getByOrganizationIdAndType(OrganizationId organizationId, String type, Integer limit, Integer offset) {
        def startKey = [organizationId.toString(), type]
        def endKey = [organizationId.toString(), type]
        return queryView('by_organization_id_type', startKey.toArray(new String()), endKey.toArray(new String()), false, limit,
                offset, false)
    }
}
