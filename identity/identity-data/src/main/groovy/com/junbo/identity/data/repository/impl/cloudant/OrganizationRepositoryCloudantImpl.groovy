package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViewQueryOptions
import com.junbo.common.id.OrganizationId
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.identity.data.repository.OrganizationRepository
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
import org.springframework.beans.factory.annotation.Required
/**
 * Created by liangfu on 5/22/14.
 */
@CompileStatic
class OrganizationRepositoryCloudantImpl extends CloudantClient<Organization> implements OrganizationRepository {
    private IdGenerator idGenerator

    @Required
    void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator
    }

    @Override
    Promise<Results<Organization>> searchByOwner(UserId ownerId, Integer limit, Integer offset) {
        return queryViewResults('by_owner_id', ownerId.toString(), limit, offset, false)
    }

    @Override
    Promise<Results<Organization>> searchByCanonicalName(String name, Integer limit, Integer offset) {
        return queryViewResults('by_canonical_name', name, limit, offset, false)
    }

    @Override
    Promise<Organization> searchByMigrateCompanyId(Long migratedCompanyId) {
        return super.queryView('by_migrate_company_id', migratedCompanyId.toString()).then { List<Organization> organizationList ->
            if (CollectionUtils.isEmpty(organizationList)) {
                return Promise.pure(null)
            }

            return Promise.pure(organizationList.get(0))
        }
    }

    @Override
    Promise<Organization> get(OrganizationId id) {
        return cloudantGet(id.toString())
    }

    @Override
    Promise<Organization> create(Organization model) {
        if (model.id == null) {
            model.id = new OrganizationId(idGenerator.nextId())
        }
        return cloudantPost(model)
    }

    @Override
    Promise<Organization> update(Organization model, Organization oldModel) {
        return cloudantPut(model, oldModel)
    }

    @Override
    Promise<Void> delete(OrganizationId id) {
        return cloudantDelete(id.toString())
    }

    @Override
    Promise<Results<Organization>> searchAll(Integer limit, Integer offset) {
        return cloudantGetAll(new CloudantViewQueryOptions(
                limit: limit,
                skip: offset,
                includeDocs: true
        ))
    }
}
