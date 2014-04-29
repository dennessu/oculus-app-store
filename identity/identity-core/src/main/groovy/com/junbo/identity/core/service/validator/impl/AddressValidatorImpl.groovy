package com.junbo.identity.core.service.validator.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.identity.core.service.validator.PiiValidator
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.spec.v1.model.Address
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
    void validate(JsonNode value) {
        ObjectMapperProvider.instance().treeToValue(value, Address)
        // todo:    User Address
    }
}
