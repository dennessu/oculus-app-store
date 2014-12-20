package com.junbo.identity.core.service.validator

import com.junbo.common.id.UserAttributeId
import com.junbo.identity.spec.v1.model.UserAttribute
import com.junbo.identity.spec.v1.option.list.UserAttributeListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by xiali_000 on 2014/12/19.
 */
@CompileStatic
public interface UserAttributeValidator {
    Promise<UserAttribute> validateForGet(UserAttributeId id)
    Promise<Void> validateForSearch(UserAttributeListOptions options)
    Promise<Void> validateForCreate(UserAttribute userAttribute)
    Promise<Void> validateForUpdate(UserAttributeId userAttributeId, UserAttribute userAttribute, UserAttribute oldUserAttribute)
}
