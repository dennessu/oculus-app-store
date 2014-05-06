package com.junbo.identity.core.service.validator.impl

import com.junbo.identity.core.service.validator.DisplayNameValidator
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.UserName
import groovy.transform.CompileStatic

/**
 * Created by kg on 3/17/14.
 */
@CompileStatic
class DisplayNameValidatorImpl implements DisplayNameValidator {
    public static final int FIRSTNAME_LASTNAME_THEN_USERNAME = 1

    public static final int LASTNAME_FIRSTNAME_THEN_USERNAME = 2

    public static final int FIRSTNAME_MIDDLENAME_LASTNAME_THEN_USERNAME = 3

    public static final int LASTNAME_MIDDLENAME_FIRSTNAME_THEN_USERNAME = 4

    @Override
    void validate(UserName name) {
        Integer displayNameType = -1
        if (name.displayName != null) {
            if (name.displayName == "${name.firstName} ${name.lastName}") {
                displayNameType = FIRSTNAME_LASTNAME_THEN_USERNAME
            }

            if (name.displayName == "${name.lastName} ${name.firstName}") {
                displayNameType = LASTNAME_FIRSTNAME_THEN_USERNAME
            }

            if (name.middleName != null) {
                if (name.displayName == "${name.firstName} ${name.middleName} ${name.lastName}") {
                    displayNameType = FIRSTNAME_MIDDLENAME_LASTNAME_THEN_USERNAME
                }

                if (name.displayName == "${name.lastName} ${name.middleName} ${name.firstName}") {
                    displayNameType = LASTNAME_MIDDLENAME_FIRSTNAME_THEN_USERNAME
                }
            }

            if (displayNameType == -1) {
                throw AppErrors.INSTANCE.fieldInvalid('displayName',
                        'FIRSTNAME_MIDDLENAME_LASTNAME or LASTNAME_MIDDLENAME_FIRSTNAME').exception()
            }
        }
    }

}
