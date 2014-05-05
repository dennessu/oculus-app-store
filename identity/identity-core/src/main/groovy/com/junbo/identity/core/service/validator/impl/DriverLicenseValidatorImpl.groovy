package com.junbo.identity.core.service.validator.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.id.UserId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.identity.core.service.util.JsonHelper
import com.junbo.identity.core.service.validator.PiiValidator
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserDriverLicense
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 4/26/14.
 */
@CompileStatic
class DriverLicenseValidatorImpl implements PiiValidator {

    private Integer minDriverLicenseLength
    private Integer maxDriverLicenseLength

    @Override
    boolean handles(String type) {
        if (type == UserPersonalInfoType.DRIVERS_LICENSE.toString()) {
            return true
        }
        return false
    }

    @Override
    Promise<Void> validate(JsonNode value, UserId userId) {
        UserDriverLicense userDriverLicense = (UserDriverLicense)JsonHelper.jsonNodeToObj(value, UserDriverLicense)
        if (userDriverLicense.value != null) {
            if (userDriverLicense.value.length() > maxDriverLicenseLength) {
                throw AppErrors.INSTANCE.fieldTooLong('value', maxDriverLicenseLength).exception()
            }
            if (userDriverLicense.value.length() < minDriverLicenseLength) {
                throw AppErrors.INSTANCE.fieldTooShort('value', minDriverLicenseLength).exception()
            }
        }

        return Promise.pure(null)
    }

    @Required
    void setMinDriverLicenseLength(Integer minDriverLicenseLength) {
        this.minDriverLicenseLength = minDriverLicenseLength
    }

    @Required
    void setMaxDriverLicenseLength(Integer maxDriverLicenseLength) {
        this.maxDriverLicenseLength = maxDriverLicenseLength
    }
}
