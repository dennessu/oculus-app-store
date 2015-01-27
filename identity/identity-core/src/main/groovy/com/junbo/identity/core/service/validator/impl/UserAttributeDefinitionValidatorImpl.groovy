package com.junbo.identity.core.service.validator.impl

import com.junbo.catalog.spec.model.item.Item
import com.junbo.catalog.spec.resource.ItemResource
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.ItemId
import com.junbo.common.id.UserAttributeDefinitionId
import com.junbo.identity.core.service.validator.UserAttributeDefinitionValidator
import com.junbo.identity.service.OrganizationService
import com.junbo.identity.service.UserAttributeDefinitionService
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.identity.spec.v1.model.UserAttributeDefinition
import com.junbo.identity.spec.v1.option.list.UserAttributeDefinitionListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
import org.springframework.beans.factory.annotation.Required

/**
 * Created by xiali_000 on 2014/12/19.
 */
@CompileStatic
class UserAttributeDefinitionValidatorImpl implements UserAttributeDefinitionValidator {

    private List<String> allowedTypes
    private UserAttributeDefinitionService userAttributeDefinitionService
    private OrganizationService organizationService
    private ItemResource itemResource

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
        return basicCheck(userAttributeDefinition)
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

        return basicCheck(userAttributeDefinition)
    }

    private Promise<Void> basicCheck(UserAttributeDefinition userAttributeDefinition) {
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

        return checkOrganizationExists(userAttributeDefinition).then {
            return checkItemsExists(userAttributeDefinition)
        }
    }

    private Promise<Void> checkOrganizationExists(UserAttributeDefinition userAttributeDefinition) {
        if (userAttributeDefinition.organizationId == null) {
            return Promise.pure(null)
        }

        return organizationService.get(userAttributeDefinition.getOrganizationId()).then { Organization organization ->
            if (organization == null) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('organization').exception()
            }

            return Promise.pure(null)
        }
    }

    private Promise<Void> checkItemsExists(UserAttributeDefinition userAttributeDefinition) {
        if (CollectionUtils.isEmpty(userAttributeDefinition.items)) {
            return Promise.pure(null)
        }

        return Promise.each(userAttributeDefinition.items) { ItemId itemId ->
            return itemResource.getItem(itemId.toString()).then { Item item ->
                if (item == null) {
                    throw AppCommonErrors.INSTANCE.fieldInvalid('items').exception()
                }

                return Promise.pure(null)
            }.recover { Throwable throwable ->
                throw AppCommonErrors.INSTANCE.fieldInvalid('items').exception()
            }
        }.then {
            return Promise.pure(null)
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

    @Required
    void setOrganizationService(OrganizationService organizationService) {
        this.organizationService = organizationService
    }

    @Required
    void setItemResource(ItemResource itemResource) {
        this.itemResource = itemResource
    }
}
