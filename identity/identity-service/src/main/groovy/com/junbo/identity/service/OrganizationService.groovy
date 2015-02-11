package com.junbo.identity.service
import com.junbo.common.id.OrganizationId
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
/**
 * Created by liangfu on 10/21/14.
 */
@CompileStatic
public interface OrganizationService {
    Promise<Organization> get(OrganizationId id)

    Promise<Organization> create(Organization model)

    Promise<Organization> update(Organization model, Organization oldModel)

    Promise<Void> delete(OrganizationId id)

    public Promise<Results<Organization>> searchByOwner(UserId ownerId, Integer limit, Integer offset)

    public Promise<Results<Organization>> searchByCanonicalName(String name, Integer limit, Integer offset)

    public Promise<Organization> searchByMigrateCompanyId(Long migratedCompanyId)

    Promise<Results<Organization>> searchAll(Integer limit, Integer offset)

    // internal use: get the default organizationId. The result is cached.
    Promise<OrganizationId> getDefaultOrganizationId();
}