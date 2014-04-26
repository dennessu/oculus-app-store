package com.junbo.identity.core.service.validator.impl

import com.junbo.identity.core.service.validator.DriverLicenseValidator
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserDriverLicense
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/26/14.
 */
@CompileStatic
class DriverLicenseValidatorImpl implements DriverLicenseValidator {

    private Integer minDriverLicenseLength
    private Integer maxDriverLicenseLength

    @Override
    void validate(UserDriverLicense userDriverLicense) {
        if (userDriverLicense.value != null) {
            if (userDriverLicense.value.length() > maxDriverLicenseLength) {
                throw AppErrors.INSTANCE.fieldTooLong('value', maxDriverLicenseLength).exception()
            }
            if (userDriverLicense.value.length() < minDriverLicenseLength) {
                throw AppErrors.INSTANCE.fieldTooShort('value', minDriverLicenseLength).exception()
            }
        }
    }
}
