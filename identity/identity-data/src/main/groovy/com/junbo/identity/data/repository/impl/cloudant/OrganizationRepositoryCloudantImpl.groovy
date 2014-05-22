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

    @Override
    protected CloudantViews getCloudantViews() {
        return views
    }

    @Override
    Promise<List<Organization>> searchByOwner(UserId ownerId, Integer limit, Integer offset) {
        def result = super.queryView('by_owner_id', ownerId.value.toString(), limit, offset, false)

        return Promise.pure(result)
    }

    @Override
    Promise<Organization> get(OrganizationId id) {
        return Promise.pure((Organization)super.cloudantGet(id.toString()))
    }

    @Override
    Promise<Organization> create(Organization model) {
        if (model.id == null) {
            model.id = new OrganizationId(idGenerator.nextId(model.ownerId.value))
        }
        return Promise.pure((Organization)super.cloudantPost(model))
    }

    @Override
    Promise<Organization> update(Organization model) {
        return Promise.pure((Organization)super.cloudantPut(model))
    }

    @Override
    Promise<Void> delete(OrganizationId id) {
        super.cloudantDelete(id.toString())
        return Promise.pure(null)
    }

    protected CloudantViews views = new CloudantViews(
            views: [
                    'by_owner_id': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    '  emit(doc.ownerId, doc._id)' +
                                    '}',
                            resultClass: String),
                   ]
    )

    @Required
    void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator
    }
}
