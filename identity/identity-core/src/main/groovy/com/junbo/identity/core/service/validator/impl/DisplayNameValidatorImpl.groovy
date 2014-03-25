package com.junbo.identity.core.service.validator.impl

import com.junbo.identity.core.service.validator.DisplayNameValidator
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.model.users.User
import groovy.transform.CompileStatic

/**
 * Created by kg on 3/17/14.
 */
@CompileStatic
/*
 * Possible values:
 * 1. firstName_lastName_then_username
 * 2. lastName_firstName_then_username
 * 3. firstName_middleName_lastName_then_username
 * 4. lastName_middleName_firstName_then_username
 * 5. username
 */
@CompileStatic
class DisplayNameValidatorImpl implements DisplayNameValidator {

    public static final int FIRSTNAME_LASTNAME_THEN_USERNAME = 1

    public static final int LASTNAME_FIRSTNAME_THEN_USERNAME = 2

    public static final int FIRSTNAME_MIDDLENAME_LASTNAME_THEN_USERNAME = 3

    public static final int LASTNAME_MIDDLENAME_FIRSTNAME_THEN_USERNAME = 4

    public static final int USERNAME = 5

    Integer getDisplayNameType(User user) {
        if (user == null) {
            throw new IllegalArgumentException('user is null')
        }

        if (user.displayName == null) {
            return FIRSTNAME_LASTNAME_THEN_USERNAME
        } else {
            if (user.name != null) {
                if (user.displayName == "${user.name.firstName} ${user.name.lastName}") {
                    return FIRSTNAME_LASTNAME_THEN_USERNAME
                }

                if (user.displayName == "${user.name.lastName} ${user.name.firstName}") {
                    return LASTNAME_FIRSTNAME_THEN_USERNAME
                }

                if (user.name.middleName != null) {
                    if (user.displayName == "${user.name.firstName} ${user.name.middleName} ${user.name.lastName}") {
                        return FIRSTNAME_MIDDLENAME_LASTNAME_THEN_USERNAME
                    }

                    if (user.displayName == "${user.name.lastName} ${user.name.middleName} ${user.name.firstName}") {
                        return LASTNAME_MIDDLENAME_FIRSTNAME_THEN_USERNAME
                    }
                }
            }

            if (user.username != null) {
                if (user.displayName == user.username) {
                    return USERNAME
                }
            }
        }


        def possibleValues = []
        if (user.name != null) {
            possibleValues.add("${user.name.firstName} ${user.name.lastName}")
            possibleValues.add("${user.name.lastName} ${user.name.firstName}")

            if (user.name.middleName != null) {
                possibleValues.add("${user.name.firstName} ${user.name.middleName} ${user.name.lastName}")
                possibleValues.add("${user.name.lastName} ${user.name.middleName} ${user.name.firstName}")
            }
        }

        if (user.username != null) {
            possibleValues.add(user.username)
        }


        throw AppErrors.INSTANCE.fieldInvalid('displayName', possibleValues.join(', ')).exception()
    }

    String getDisplayName(User user) {
        if (user == null) {
            throw new IllegalArgumentException('user is null')
        }

        if (user.displayNameType == null || user.displayNameType == FIRSTNAME_LASTNAME_THEN_USERNAME) {
            if (user.name != null) {
                return "${user.name.firstName} ${user.name.lastName}"
            }

            if (user.username != null) {
                return user.username
            }

            return null
        }

        if (user.displayNameType == LASTNAME_FIRSTNAME_THEN_USERNAME) {
            if (user.name != null) {
                return "${user.name.lastName} ${user.name.firstName}"
            }

            if (user.username != null) {
                return user.username
            }

            return null
        }

        if (user.displayNameType == FIRSTNAME_MIDDLENAME_LASTNAME_THEN_USERNAME) {
            if (user.name != null) {
                if (user.name.middleName != null) {
                    return "${user.name.firstName} ${user.name.middleName} ${user.name.lastName}"
                }

                return "${user.name.firstName} ${user.name.lastName}"
            }

            if (user.username != null) {
                return user.username
            }

            return null
        }

        if (user.displayNameType == LASTNAME_MIDDLENAME_FIRSTNAME_THEN_USERNAME) {
            if (user.name != null) {
                if (user.name.middleName != null) {
                    return "${user.name.lastName} ${user.name.middleName} ${user.name.firstName}"
                }

                return "${user.name.lastName} ${user.name.firstName}"
            }

            if (user.username != null) {
                return user.username
            }

            return null
        }

        if (user.displayNameType == USERNAME) {
            if (user.username != null) {
                return user.username
            }

            return null
        }

        return null
    }
}
