package com.junbo.identity.data.repository

import com.junbo.common.id.UserAttributeDefinitionId
import com.junbo.identity.spec.v1.model.UserAttributeDefinition
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by xiali_000 on 2014/12/19.
 */
@CompileStatic
public interface UserAttributeDefinitionRepository {
    // todo:    Need to set the results to Results, not list
    Promise<UserAttributeDefinition> get(UserAttributeDefinitionId userAttributeDefinitionId)

    Promise<UserAttributeDefinition> create(UserAttributeDefinition userAttributeDefinition)

    Promise<UserAttributeDefinition> update(UserAttributeDefinition newUserAttributeDefinition,
                                            UserAttributeDefinition oldUserAttributeDefinition)

    Promise<Void> delete(UserAttributeDefinitionId userAttributeDefinitionId)

    Promise<List<UserAttributeDefinition>> getAll(Integer limit, Integer offset)
}