package com.junbo.identity.data.repository

import com.junbo.common.id.UserAttributeDefinitionId
import com.junbo.common.id.UserAttributeId
import com.junbo.common.id.UserId
import com.junbo.identity.spec.v1.model.UserAttribute
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.repo.BaseRepository
import com.sun.org.apache.xpath.internal.operations.Bool
import groovy.transform.CompileStatic

/**
 * Created by xiali_000 on 2014/12/19.
 */
@CompileStatic
public interface UserAttributeRepository extends BaseRepository<UserAttribute, UserAttributeId> {
    Promise<List<UserAttribute>> searchByUserId(UserId userId, Integer limit, Integer offset)

    Promise<List<UserAttribute>> searchByUserAttributeDefinitionId(UserAttributeDefinitionId userAttributeDefinitionId,
                                                                      Integer limit, Integer offset)

    Promise<List<UserAttribute>> searchByUserIdAndAttributeDefinitionId(UserId userId,
                                                                           UserAttributeDefinitionId userAttributeDefinitionId, Integer limit, Integer offset)

    Promise<List<UserAttribute>> searchByActive(Boolean isActive, Integer limit, Integer offset)
}