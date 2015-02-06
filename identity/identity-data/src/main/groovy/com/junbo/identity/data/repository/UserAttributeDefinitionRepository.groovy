package com.junbo.identity.data.repository

import com.junbo.common.id.OrganizationId
import com.junbo.common.id.UserAttributeDefinitionId
import com.junbo.identity.spec.v1.model.UserAttributeDefinition
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.repo.BaseRepository
import groovy.transform.CompileStatic

/**
 * Created by xiali_000 on 2014/12/19.
 */
@CompileStatic
public interface UserAttributeDefinitionRepository extends BaseRepository<UserAttributeDefinition, UserAttributeDefinitionId> {

    Promise<List<UserAttributeDefinition>> getAll(Integer limit, Integer offset)

    Promise<List<UserAttributeDefinition>> getByOrganizationId(OrganizationId organizationId, Integer limit, Integer offset);

    Promise<List<UserAttributeDefinition>> getByOrganizationIdAndType(OrganizationId organizationId, String type, Integer limit, Integer offset);
}