package com.junbo.identity.service

import com.junbo.common.id.UserAttributeDefinitionId
import com.junbo.common.id.UserAttributeId
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.UserAttribute
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by xiali_000 on 2014/12/19.
 */
@CompileStatic
interface UserAttributeService {

    Promise<UserAttribute> get(UserAttributeId userAttributeId)

    Promise<UserAttribute> update(UserAttribute userAttribute, UserAttribute oldUserAttribute)

    Promise<UserAttribute> create(UserAttribute userAttribute)

    Promise<Void> delete(UserAttributeId userAttributeId)

    Promise<Results<UserAttribute>> searchByUserAttributeDefinitionId(
            UserAttributeDefinitionId userAttributeDefinitionId, Boolean activeOnly, Integer limit, Integer offset)

    Promise<Results<UserAttribute>> searchByUserIdAndAttributeDefinitionId(UserId userId,
            UserAttributeDefinitionId userAttributeDefinitionId, Boolean activeOnly, Integer limit, Integer offset)
}
