package com.junbo.identity.core.service.validator.impl

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserAttributeDefinitionId
import com.junbo.identity.core.service.validator.UserAttributeDefinitionValidator
import com.junbo.identity.service.UserAttributeDefinitionService
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserAttributeDefinition
import com.junbo.identity.spec.v1.option.list.UserAttributeDefinitionListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by xiali_000 on 2014/12/19.
 */
@CompileStatic
class UserAttributeDefinitionValidatorImpl implements UserAttributeDefinitionValidator {

    private List<String> allowedTypes
    private UserAttributeDefinitionService userAttributeDefinitionService

    @Override
    Promise<UserAttributeDefinition> validateForGet(UserAttributeDefinitionId id) {
        if (id == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('id').exception()
        }

        return userAttributeDefinitionService.get(id).then { UserAttributeDefinition userAttributeDefinition ->
            if (userAttributeDefinition == null) {
                throw AppErrors.INSTANCE.userAttributeDefinitionNotFound(id).exception()
            }

            return Promise.pure(userAttributeDefinition)
        }
    }

    @Override
    Promise<Void> validateForSearch(UserAttributeDefinitionListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(UserAttributeDefinition userAttributeDefinition) {
        if (userAttributeDefinition == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        if (userAttributeDefinition.id != null) {
            throw AppCommonErrors.INSTANCE.fieldNotWritable('id').exception()
        }
        basicCheck(userAttributeDefinition)

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForUpdate(UserAttributeDefinitionId userAttributeDefinitionId,
                UserAttributeDefinition userAttributeDefinition, UserAttributeDefinition oldUserAttributeDefinition) {
        if (userAttributeDefinition == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }
        if (userAttributeDefinitionId != userAttributeDefinition.getId()
         || userAttributeDefinition.getId() != oldUserAttributeDefinition.getId()) {
            throw AppCommonErrors.INSTANCE.fieldNotWritable('id').exception()
        }

        basicCheck(userAttributeDefinition)

        return Promise.pure(null)
    }

    private void basicCheck(UserAttributeDefinition userAttributeDefinition) {
        if (userAttributeDefinition.type == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('type').exception()
        }

        if (!allowedTypes.any{ String allowedType ->
            return allowedType.equalsIgnoreCase(userAttributeDefinition.type)
        }) {
            throw AppCommonErrors.INSTANCE.fieldInvalidEnum('type', allowedTypes.join(',')).exception()
        }

        if (userAttributeDefinition.description == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('description').exception()
        }
    }

    @Required
    void setAllowedTypes(List<String> allowedTypes) {
        this.allowedTypes = allowedTypes
    }

    @Required
    void setUserAttributeDefinitionService(UserAttributeDefinitionService userAttributeDefinitionService) {
        this.userAttributeDefinitionService = userAttributeDefinitionService
    }
}
