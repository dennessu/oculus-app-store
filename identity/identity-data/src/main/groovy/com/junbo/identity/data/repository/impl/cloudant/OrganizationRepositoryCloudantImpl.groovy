package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.OrganizationId
import com.junbo.common.id.UserId
import com.junbo.identity.data.repository.OrganizationRepository
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import groovy.transform.CompileStatic
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
    protected CloudantViews getCloudantViews() {
        return views
    }

    @Override
    Promise<List<Organization>> searchByOwner(UserId ownerId, Integer limit, Integer offset) {
        return super.queryView('by_owner_id', ownerId.value.toString(), limit, offset, false)
    }

    @Override
    Promise<List<Organization>> searchByCanonicalName(String name, Integer limit, Integer offset) {
        def result = super.queryView('by_canonical_name', name, limit, offset, false)

        return result
    }

    @Override
    Promise<Organization> get(OrganizationId id) {
        return super.cloudantGet(id.toString())
    }

    @Override
    Promise<Organization> create(Organization model) {
        if (model.id == null) {
            model.id = new OrganizationId(idGenerator.nextId())
        }
        return super.cloudantPost(model)
    }

    @Override
    Promise<Organization> update(Organization model) {
        return super.cloudantPut(model)
    }

    @Override
    Promise<Void> delete(OrganizationId id) {
        return super.cloudantDelete(id.toString())
    }

    protected CloudantViews views = new CloudantViews(
            views: [
                    'by_owner_id': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.ownerId, doc._id)' +
                                    '}',
                            resultClass: String),
                    'by_canonical_name': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.canonicalName, doc._id)' +
                                    '}',
                            resultClass: String)
                   ]
    )
}
