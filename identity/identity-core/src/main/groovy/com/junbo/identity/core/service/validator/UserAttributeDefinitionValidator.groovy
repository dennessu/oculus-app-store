package com.junbo.identity.core.service.validator

import com.junbo.common.id.UserAttributeDefinitionId
import com.junbo.identity.spec.v1.model.UserAttributeDefinition
import com.junbo.identity.spec.v1.option.list.UserAttributeDefinitionListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by xiali_000 on 2014/12/19.
 */
@CompileStatic
public interface UserAttributeDefinitionValidator {
    Promise<UserAttributeDefinition> validateForGet(UserAttributeDefinitionId id)
    Promise<Void> validateForSearch(UserAttributeDefinitionListOptions options)
    Promise<Void> validateForCreate(UserAttributeDefinition userAttributeDefinition)
    Promise<Void> validateForUpdate(UserAttributeDefinitionId userAttributeDefinitionId,
                    UserAttributeDefinition userAttributeDefinition, UserAttributeDefinition oldUserAttributeDefinition)
}