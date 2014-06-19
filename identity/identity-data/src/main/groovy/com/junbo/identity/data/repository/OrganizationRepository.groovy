package com.junbo.identity.data.repository

import com.junbo.common.id.OrganizationId
import com.junbo.common.id.UserId
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import com.junbo.sharding.repo.BaseRepository

/**
 * Created by liangfu on 5/22/14.
 */
public interface OrganizationRepository extends BaseRepository<Organization, OrganizationId> {

    @ReadMethod
    public Promise<List<Organization>> searchByOwner(UserId ownerId, Integer limit, Integer offset)

    @ReadMethod
    public Promise<Organization> searchByCanonicalName(String name)

    @ReadMethod
    public Promise<Organization> searchByMigrateCompanyId(Long migratedCompanyId)
}