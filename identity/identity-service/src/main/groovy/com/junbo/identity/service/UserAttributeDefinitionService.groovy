package com.junbo.identity.service

import com.junbo.common.id.OrganizationId
import com.junbo.common.id.UserAttributeDefinitionId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.UserAttributeDefinition
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by xiali_000 on 2014/12/19.
 */
@CompileStatic
public interface UserAttributeDefinitionService {
    Promise<UserAttributeDefinition> get(UserAttributeDefinitionId userAttributeDefinitionId)

    Promise<UserAttributeDefinition> create(UserAttributeDefinition userAttributeDefinition)

    Promise<UserAttributeDefinition> update(UserAttributeDefinition newUserAttributeDefinition,
                                            UserAttributeDefinition oldUserAttributeDefinition)

    Promise<Void> delete(UserAttributeDefinitionId userAttributeDefinitionId)

    Promise<Results<UserAttributeDefinition>> getAll(Integer limit, Integer offset)

    Promise<Results<UserAttributeDefinition>> getByOrganizationId(OrganizationId organizationId, Integer limit, Integer offset);

    Promise<UserAttributeDefinition> getByOrganizationIdAndType(OrganizationId organizationId, String type);
}