package com.junbo.identity.core.service.validator.impl

import com.junbo.common.id.AddressId
import com.junbo.common.id.UserPiiId
import com.junbo.identity.core.service.validator.*
import com.junbo.identity.data.repository.UserPiiRepository
import com.junbo.identity.data.repository.UserRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Address
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserEmail
import com.junbo.identity.spec.v1.model.UserPhoneNumber
import com.junbo.identity.spec.v1.model.UserPii
import com.junbo.identity.spec.v1.option.list.UserPiiListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 4/10/14.
 */
@CompileStatic
class UserPiiValidatorImpl implements UserPiiValidator {

    private UserRepository userRepository
    private UserPiiRepository userPiiRepository

    private NameValidator nameValidator
    private UserEmailValidator userEmailValidator
    private UserPhoneNumberValidator userPhoneNumberValidator
    private BirthdayValidator birthdayValidator
    private DisplayNameValidator displayNameValidator
    private AddressValidator addressValidator

    private List<String> allowedGenders

    @Override
    Promise<UserPii> validateForGet(UserPiiId userPiiId) {
        if (userPiiId == null) {
            throw new IllegalArgumentException('userPiiId is null')
        }

        return userPiiRepository.get(userPiiId).then { UserPii userPii ->
            if (userPii == null) {
                throw AppErrors.INSTANCE.userPiiNotFound(userPiiId).exception()
            }

            return Promise.pure(userPii)
        }
    }

    @Override
    Promise<Void> validateForSearch(UserPiiListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('userPiiListOptions is null')
        }
        if (options.userId == null && options.email == null) {
            throw AppErrors.INSTANCE.parameterRequired('userId or email').exception()
        }

        if (options.userId != null && options.email != null) {
            throw AppErrors.INSTANCE.parameterInvalid('userId and email can\'t search together.').exception()
        }

        return Promise.pure(null)
    }

    Promise<Void> checkEmailForCreate(UserPii userPii) {
        if (userPii.emails != null) {
            userPii.emails.each { Map.Entry entry ->
                UserEmail userEmail = (UserEmail) entry.value

                userPiiRepository.search(new UserPiiListOptions(
                        email: userEmail.value
                )).then { List<UserPii> userPiiList ->
                    if (!CollectionUtils.isEmpty(userPiiList)) {
                        throw AppErrors.INSTANCE.fieldDuplicate('email').exception()
                    }
                }
            }
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(UserPii userPii) {

        if (userPii.displayNameType != null) {
            throw AppErrors.INSTANCE.fieldNotWritable('displayNameType').exception()
        }

        return checkBasicUserPiiInfo(userPii).then {
            if (userPii.id != null) {
                throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
            }

            return userPiiRepository.search(new UserPiiListOptions(
                    userId: userPii.userId
            )).then { List<UserPii> existing ->
                if (!CollectionUtils.isEmpty(existing)) {
                    throw AppErrors.INSTANCE.fieldDuplicate('userId').exception()
                }

                return checkEmailForCreate(userPii)
            }
        }
    }

    @Override
    Promise<Void> validateForUpdate(UserPiiId userPiiId, UserPii userPii, UserPii oldUserPii) {
        if (userPiiId == null) {
            throw new IllegalArgumentException('userPiiId is null')
        }

        if (userPii.id != userPiiId) {
            throw AppErrors.INSTANCE.fieldInvalid('id', userPiiId.toString()).exception()
        }
        if (userPii.id != oldUserPii.id) {
            throw AppErrors.INSTANCE.fieldInvalid('id', oldUserPii.id.toString()).exception()
        }

        return checkBasicUserPiiInfo(userPii).then {
            return checkEmailsForUpdate(userPii, oldUserPii)
        }
    }

    private Promise<Void> checkEmailsForUpdate(UserPii userPii, UserPii oldUserPii) {
        if (userPii.emails != null) {
            userPii.emails.each { Map.Entry entry ->
                UserEmail userEmail = (UserEmail)entry.value
                boolean matches = false
                oldUserPii.emails.each { Map.Entry oldEntry ->
                    UserEmail oldUserEmail = (UserEmail)oldEntry.value
                    if (oldUserEmail.value == userEmail.value) {
                        matches = true
                    }
                }

                if (!matches) {
                    userPiiRepository.search(new UserPiiListOptions(
                            email: userEmail.value
                    )).then { List<UserPii> userPiiList ->
                        if (!CollectionUtils.isEmpty(userPiiList)) {
                            throw AppErrors.INSTANCE.fieldInvalid('email').exception()
                        }
                    }
                }
            }
        }
        return Promise.pure(null)
    }

    private Promise<Object> checkAddressBook(UserPii userPii) {
        if (userPii.addressBook != null) {
            return Promise.each (userPii.addressBook.iterator(), new Promise.Func<AddressId, Promise<Object>>() {
                @Override
                Promise<Object> apply(AddressId addressId) {
                    return addressValidator.validateForGet(addressId).then { Address address ->
                        if (address.userId != userPii.userId) {
                            return Promise.throwing(AppErrors.INSTANCE.fieldInvalid('addressId').exception())
                        }

                        return Promise.pure(null)
                    }
                }
            }
            )
        }

        return Promise.pure(null)
    }

    private Promise<Void> checkBasicUserPiiInfo(UserPii userPii) {
        if (userPii == null) {
            throw new IllegalArgumentException('userPii is null')
        }

        if (userPii.birthday != null) {
            if (!birthdayValidator.isValidBirthday(userPii.birthday)) {
                throw AppErrors.INSTANCE.fieldInvalid('birthday').exception()
            }
        }

        if (userPii.gender != null) {
            if (!(userPii.gender in allowedGenders)) {
                throw AppErrors.INSTANCE.fieldInvalid('gender', allowedGenders.join(',')).exception()
            }
        }

        if (userPii.name != null) {
            nameValidator.validateName(userPii.name)
        }

        if (userPii.emails != null) {
            userPii.emails.each { Map.Entry entry ->
                String type = (String)entry.key
                UserEmail userEmail = (UserEmail)entry.value
                userEmail.setType(type)

                userEmailValidator.validate(userEmail)
            }
        }

        if (userPii.phoneNumbers != null) {
            userPii.phoneNumbers.each { Map.Entry entry ->
                String type = (String)entry.key
                UserPhoneNumber userPhoneNumber = (UserPhoneNumber)entry.value
                userPhoneNumber.setType(type)

                userPhoneNumberValidator.validate(userPhoneNumber)
            }
        }

        return checkAddressBook(userPii).then {
            if (userPii.userId == null) {
                throw AppErrors.INSTANCE.fieldRequired('userId').exception()
            }

            return userRepository.get(userPii.userId).then { User existingUser ->
                if (existingUser == null) {
                    throw AppErrors.INSTANCE.userNotFound(userPii.userId).exception()
                }
                if (existingUser.active == null || existingUser.active == false) {
                    throw AppErrors.INSTANCE.userInInvalidStatus(userPii.userId).exception()
                }

                userPii.displayNameType = displayNameValidator.getDisplayNameType(existingUser, userPii)

                return Promise.pure(null)
            }
        }
    }

    @Required
    void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository
    }

    @Required
    void setUserPiiRepository(UserPiiRepository userPiiRepository) {
        this.userPiiRepository = userPiiRepository
    }

    @Required
    void setNameValidator(NameValidator nameValidator) {
        this.nameValidator = nameValidator
    }

    @Required
    void setUserEmailValidator(UserEmailValidator userEmailValidator) {
        this.userEmailValidator = userEmailValidator
    }

    @Required
    void setUserPhoneNumberValidator(UserPhoneNumberValidator userPhoneNumberValidator) {
        this.userPhoneNumberValidator = userPhoneNumberValidator
    }

    @Required
    void setBirthdayValidator(BirthdayValidator birthdayValidator) {
        this.birthdayValidator = birthdayValidator
    }

    @Required
    void setDisplayNameValidator(DisplayNameValidator displayNameValidator) {
        this.displayNameValidator = displayNameValidator
    }

    @Required
    void setAddressValidator(AddressValidator addressValidator) {
        this.addressValidator = addressValidator
    }

    @Required
    void setAllowedGenders(List<String> allowedGenders) {
        this.allowedGenders = allowedGenders
    }
}
