package com.junbo.identity.core.service.validator.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.id.OrganizationId
import com.junbo.common.id.UserId
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.core.service.validator.PiiValidator
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Address
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by xmchen on 14-4-15.
 */
@CompileStatic
class AddressValidatorImpl implements PiiValidator {

    @Override
    boolean handles(String type) {
        if (type == UserPersonalInfoType.ADDRESS.toString()) {
            return true
        }
        return false
    }

    @Override
    Promise<Void> validateCreate(JsonNode value, UserId userId, OrganizationId organizationId) {
        JsonHelper.jsonNodeToObj(value, Address)

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateUpdate(JsonNode value, JsonNode oldValue) {
        Address address = (Address)JsonHelper.jsonNodeToObj(value, Address)
        Address oldAddress = (Address)JsonHelper.jsonNodeToObj(oldValue, Address)

        if (address != oldAddress) {
            throw AppErrors.INSTANCE.fieldInvalidException('value', 'Value can\'t be updated.').exception()
        }

        return Promise.pure(null)
    }
}
