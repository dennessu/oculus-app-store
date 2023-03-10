package com.junbo.identity.core.service.validator.impl

import com.junbo.authorization.AuthorizeContext
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.OrganizationId
import com.junbo.common.id.UserAttributeDefinitionId
import com.junbo.common.id.UserAttributeId
import com.junbo.common.id.UserId
import com.junbo.identity.core.service.validator.UserAttributeValidator
import com.junbo.identity.service.OrganizationService
import com.junbo.identity.service.UserAttributeDefinitionService
import com.junbo.identity.service.UserAttributeService
import com.junbo.identity.service.UserService
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserAttribute
import com.junbo.identity.spec.v1.model.UserAttributeDefinition
import com.junbo.identity.spec.v1.option.list.UserAttributeListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by xiali_000 on 2014/12/19.
 */
@CompileStatic
class UserAttributeValidatorImpl implements UserAttributeValidator {

    UserAttributeService userAttributeService
    UserAttributeDefinitionService userAttributeDefinitionService
    UserService userService
    OrganizationService organizationService

    @Override
    Promise<UserAttribute> validateForGet(UserAttributeId id) {
        if (id == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('id').exception()
        }

        return userAttributeService.get(id).then { UserAttribute userAttribute ->
            if (userAttribute == null) {
                throw AppErrors.INSTANCE.userAttributeNotFound(id).exception()
            }

            return Promise.pure(userAttribute)
        }
    }

    @Override
    Promise<UserAttributeDefinition> validateForSearch(UserAttributeListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        if (options.activeOnly != null) {
            if (options.userId == null) {
                throw AppCommonErrors.INSTANCE.parameterRequired('userId').exception()
            }
        }
        return validateUserAttributeDefinitionExists(options.userAttributeDefinitionId, options.type, options.organizationId);
    }

    @Override
    Promise<Void> validateForCreate(UserAttribute userAttribute) {
        if (userAttribute == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        if (userAttribute.id != null) {
            throw AppCommonErrors.INSTANCE.fieldNotWritable('id').exception()
        }

        if (userAttribute.isActive != null) {
            throw AppCommonErrors.INSTANCE.fieldNotWritable('activeOnly').exception()
        }

        if (userAttribute.isSuspended) {
            throw AppCommonErrors.INSTANCE.fieldNotWritable('isSuspended').exception()
        }

        if (userAttribute.useCount != null && userAttribute.useCount < 0) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('useCount', 'useCount should be larger than 0').exception()
        }

        return validateUserExists(userAttribute.userId).then {
            return validateUserAttributeDefinitionExists(userAttribute.userAttributeDefinitionId,
                    userAttribute.type, userAttribute.organizationId).then { UserAttributeDefinition attributeDefinition ->
                userAttribute.userAttributeDefinitionId = attributeDefinition.getId()
                userAttribute.organizationId = attributeDefinition.organizationId;
                userAttribute.type = attributeDefinition.type;
                return Promise.pure()
            }
        }
    }

    @Override
    Promise<Void> validateForUpdate(UserAttributeId userAttributeId, UserAttribute userAttribute, UserAttribute oldUserAttribute) {
        if (userAttributeId != userAttribute.getId() || userAttribute.getId() != oldUserAttribute.getId()) {
            throw AppCommonErrors.INSTANCE.fieldNotWritable('id').exception()
        }

        if (userAttribute.userAttributeDefinitionId != oldUserAttribute.userAttributeDefinitionId) {
            throw AppCommonErrors.INSTANCE.fieldNotWritable('definition').exception()
        }

        if (userAttribute.organizationId != oldUserAttribute.organizationId) {
            throw AppCommonErrors.INSTANCE.fieldNotWritable('organization').exception()
        }

        if (userAttribute.type != oldUserAttribute.type) {
            throw AppCommonErrors.INSTANCE.fieldNotWritable('type').exception()
        }

        if (userAttribute.isActive != oldUserAttribute.isActive) {
            throw AppCommonErrors.INSTANCE.fieldNotWritable('isActive').exception()
        }

        if (userAttribute.isSuspended != oldUserAttribute.isSuspended) {
            if (!AuthorizeContext.hasRights('admin')) {
                throw AppCommonErrors.INSTANCE.forbiddenWithMessage('admin right is necessary to set isSuspended').exception()
            }
        }

        if (userAttribute.useCount != null && userAttribute.useCount < 0) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('useCount', 'useCount should be larger than 0').exception()
        }

        return Promise.pure(null)
    }

    private Promise<User> validateUserExists(UserId userId) {
        if (userId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('userId').exception()
        }

        return userService.get(userId).then { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.userNotFound(userId).exception()
            }

            return Promise.pure(user)
        }
    }

    private Promise<UserAttributeDefinition> validateUserAttributeDefinitionExists(UserAttributeDefinitionId userAttributeDefinitionId, String type, OrganizationId organizationId) {
        Promise<UserAttributeDefinition> userAttrDefGet = null;
        if (userAttributeDefinitionId != null) {
            if (type != null) {
                throw AppCommonErrors.INSTANCE.fieldInvalid("type", "type cannot be used together with definitionId").exception();
            } else if (organizationId != null) {
                throw AppCommonErrors.INSTANCE.fieldInvalid("organizationId", "organizationId cannot be used together with definitionId").exception();
            }
            userAttrDefGet = userAttributeDefinitionService.get(userAttributeDefinitionId)
        } else {
            if (type == null) {
                throw AppCommonErrors.INSTANCE.fieldRequired("type or definitionId").exception();
            }
            Promise<OrganizationId> getOrgId = null;
            if (organizationId == null) {
                getOrgId = organizationService.getDefaultOrganizationId();
            } else {
                getOrgId = Promise.pure(organizationId);
            }
            userAttrDefGet = getOrgId.then { OrganizationId orgGetResult ->
                return userAttributeDefinitionService.getByOrganizationIdAndType(orgGetResult, type);
            }
        }
        return userAttrDefGet.then { UserAttributeDefinition userAttributeDefinition ->
            if (userAttributeDefinition == null) {
                throw AppErrors.INSTANCE.userAttributeDefinitionNotFound(userAttributeDefinitionId).exception()
            }
            return Promise.pure(userAttributeDefinition)
        }
    }

    @Required
    void setUserAttributeService(UserAttributeService userAttributeService) {
        this.userAttributeService = userAttributeService
    }

    @Required
    void setUserAttributeDefinitionService(UserAttributeDefinitionService userAttributeDefinitionService) {
        this.userAttributeDefinitionService = userAttributeDefinitionService
    }

    @Required
    void setUserService(UserService userService) {
        this.userService = userService
    }
}
