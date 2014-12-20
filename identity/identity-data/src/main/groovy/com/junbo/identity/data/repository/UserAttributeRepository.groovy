package com.junbo.identity.data.repository

import com.junbo.common.id.UserAttributeDefinitionId
import com.junbo.common.id.UserAttributeId
import com.junbo.common.id.UserId
import com.junbo.identity.spec.v1.model.UserAttribute
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by xiali_000 on 2014/12/19.
 */
@CompileStatic
public interface UserAttributeRepository {
// todo:    Need to set the results to Results, not list
    Promise<UserAttribute> get(UserAttributeId userAttributeId)

    Promise<UserAttribute> update(UserAttribute userAttribute, UserAttribute oldUserAttribute)

    Promise<UserAttribute> create(UserAttribute userAttribute)

    Promise<Void> delete(UserAttributeId userAttributeId)

    Promise<List<UserAttribute>> searchByUserId(UserId userId, Integer limit, Integer offset)

    Promise<List<UserAttribute>> searchByUserAttributeDefinitionId(UserAttributeDefinitionId userAttributeDefinitionId,
                                                                      Integer limit, Integer offset)

    Promise<List<UserAttribute>> searchByUserIdAndAttributeDefinitionId(UserId userId,
                                                                           UserAttributeDefinitionId userAttributeDefinitionId, Integer limit, Integer offset)
}