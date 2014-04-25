package com.junbo.identity.core.service.validator.impl

import com.junbo.identity.core.service.validator.DisplayNameValidator
import com.junbo.identity.spec.v1.model.User
import groovy.transform.CompileStatic

/**
 * Created by kg on 3/17/14.
 */
@CompileStatic
@SuppressWarnings('EmptyMethod')
class DisplayNameValidatorImpl implements DisplayNameValidator {
/*
    public static final int FIRSTNAME_LASTNAME_THEN_USERNAME = 1

    public static final int LASTNAME_FIRSTNAME_THEN_USERNAME = 2

    public static final int FIRSTNAME_MIDDLENAME_LASTNAME_THEN_USERNAME = 3

    public static final int LASTNAME_MIDDLENAME_FIRSTNAME_THEN_USERNAME = 4

    public static final int USERNAME = 5
*/
    Integer getDisplayNameType(User user) {
        /*
        if (user == null) {
            throw new IllegalArgumentException('user is null')
        }
        if (userPii == null) {
            throw new IllegalArgumentException('userPii is null')
        }

        if (userPii.displayName == null) {
            return FIRSTNAME_LASTNAME_THEN_USERNAME
        }
        if (userPii.name != null) {
            if (userPii.displayName == "${userPii.name.firstName} ${userPii.name.lastName}") {
                return FIRSTNAME_LASTNAME_THEN_USERNAME
            }

            if (userPii.displayName == "${userPii.name.lastName} ${userPii.name.firstName}") {
                return LASTNAME_FIRSTNAME_THEN_USERNAME
            }

            if (userPii.name.middleName != null) {
                if (userPii.displayName ==
                        "${userPii.name.firstName} ${userPii.name.middleName} ${userPii.name.lastName}") {
                    return FIRSTNAME_MIDDLENAME_LASTNAME_THEN_USERNAME
                }

                if (userPii.displayName ==
                        "${userPii.name.lastName} ${userPii.name.middleName} ${userPii.name.firstName}") {
                    return LASTNAME_MIDDLENAME_FIRSTNAME_THEN_USERNAME
                }
            }
        }

        if (user.username != null) {
            if (userPii.displayName == user.username) {
                return USERNAME
            }
        }

        def possibleValues = []
        if (userPii.name != null) {
            possibleValues.add("${userPii.name.firstName} ${userPii.name.lastName}")
            possibleValues.add("${userPii.name.lastName} ${userPii.name.firstName}")

            if (userPii.name.middleName != null) {
                possibleValues.add("${userPii.name.firstName} ${userPii.name.middleName} ${userPii.name.lastName}")
                possibleValues.add("${userPii.name.lastName} ${userPii.name.middleName} ${userPii.name.firstName}")
            }
        }

        if (user.username != null) {
            possibleValues.add(user.username)
        }


        throw AppErrors.INSTANCE.fieldInvalid('displayName', possibleValues.join(', ')).exception()
        */
        return null
    }

    String getDisplayName(User user) {
        /*
        if (user == null) {
            throw new IllegalArgumentException('user is null')
        }

        if (userPii == null) {
            throw new IllegalArgumentException('userPii is null')
        }

        if (userPii.displayNameType == null || userPii.displayNameType == FIRSTNAME_LASTNAME_THEN_USERNAME) {
            if (userPii.name != null) {
                return "${userPii.name.firstName} ${userPii.name.lastName}"
            }

            if (user.username != null) {
                return user.username
            }

            return null
        }

        if (userPii.displayNameType == LASTNAME_FIRSTNAME_THEN_USERNAME) {
            if (userPii.name != null) {
                return "${userPii.name.lastName} ${userPii.name.firstName}"
            }

            if (user.username != null) {
                return user.username
            }

            return null
        }

        if (userPii.displayNameType == FIRSTNAME_MIDDLENAME_LASTNAME_THEN_USERNAME) {
            if (userPii.name != null) {
                if (userPii.name.middleName != null) {
                    return "${userPii.name.firstName} ${userPii.name.middleName} ${userPii.name.lastName}"
                }

                return "${userPii.name.firstName} ${userPii.name.lastName}"
            }

            if (user.username != null) {
                return user.username
            }

            return null
        }

        if (userPii.displayNameType == LASTNAME_MIDDLENAME_FIRSTNAME_THEN_USERNAME) {
            if (userPii.name != null) {
                if (userPii.name.middleName != null) {
                    return "${userPii.name.lastName} ${userPii.name.middleName} ${userPii.name.firstName}"
                }

                return "${userPii.name.lastName} ${userPii.name.firstName}"
            }

            if (user.username != null) {
                return user.username
            }

            return null
        }

        if (userPii.displayNameType == USERNAME) {
            if (user.username != null) {
                return user.username
            }

            return null
        }

        return null
        */
    }
}
