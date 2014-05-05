package com.junbo.identity.core.service.validator.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.id.UserId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.identity.core.service.util.JsonHelper
import com.junbo.identity.core.service.validator.PiiValidator
import com.junbo.identity.data.identifiable.UserPersonalInfoType
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
    Promise<Void> validate(JsonNode value, UserId userId) {
        Address address = (Address)JsonHelper.jsonNodeToObj(value, Address)

        return Promise.pure(null)
        // todo:    User Address
    }
}
