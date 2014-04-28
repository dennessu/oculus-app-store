package com.junbo.identity.core.service.validator.impl

import com.junbo.common.id.UserPersonalInfoId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.identity.core.service.validator.*
import com.junbo.identity.data.identifiable.UserPersonalInfoType
import com.junbo.identity.data.repository.UserPersonalInfoRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.*
import com.junbo.identity.spec.v1.option.list.UserPersonalInfoListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 4/26/14.
 */
@CompileStatic
class UserPersonalInfoValidatorImpl implements UserPersonalInfoValidator {

    private UserPersonalInfoRepository userPersonalInfoRepository

    private AddressValidator addressValidator
    private UserEmailValidator userEmailValidator
    private UserPhoneNumberValidator userPhoneNumberValidator
    private NameValidator nameValidator
    private BirthdayValidator birthdayValidator
    private SMSValidator smsValidator
    private QQValidator qqValidator
    private PassportValidator passportValidator
    private GovernmentIDValidator governmentIDValidator
    private DriverLicenseValidator driverLicenseValidator
    private GenderValidator genderValidator
    private UserWhatsAppValidator userWhatsAppValidator

    private Integer minLabelLength
    private Integer maxLabelLength

    @Override
    Promise<UserPersonalInfo> validateForGet(UserPersonalInfoId userPersonalInfoId) {
        if (userPersonalInfoId == null) {
            throw new IllegalArgumentException('userPersonalInfoId is null')
        }

        return userPersonalInfoRepository.get(userPersonalInfoId).then { UserPersonalInfo userPersonalInfo ->
            if (userPersonalInfo == null) {
                throw AppErrors.INSTANCE.userPersonalInfoNotFound(userPersonalInfoId).exception()
            }

            return Promise.pure(userPersonalInfo)
        }
    }

    @Override
    Promise<Void> validateForSearch(UserPersonalInfoListOptions options) {
        //todo: How to implement the search API?
        //todo: How many field needs to be supported?
        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(UserPersonalInfo userPersonalInfo) {
        if (userPersonalInfo == null) {
            throw new IllegalArgumentException('userPersonalInfo is null')
        }

        if (userPersonalInfo.id != null) {
            throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
        }
        checkBasicPersonalInfo(userPersonalInfo)
        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForUpdate(UserPersonalInfo userPersonalInfo, UserPersonalInfo oldUserPersonalInfo) {
        if (userPersonalInfo == null) {
            throw new IllegalArgumentException('userPersonalInfo is null')
        }

        if (userPersonalInfo.id != oldUserPersonalInfo.id) {
            throw AppErrors.INSTANCE.fieldInvalid('id', oldUserPersonalInfo.id.toString()).exception()
        }

        if (userPersonalInfo.id == null) {
            throw AppErrors.INSTANCE.fieldRequired('id').exception()
        }
        checkBasicPersonalInfo(userPersonalInfo)
        return Promise.pure(null)
    }

    void checkBasicPersonalInfo(UserPersonalInfo userPersonalInfo) {
        if (userPersonalInfo == null) {
            throw new IllegalArgumentException('userPersonalInfo is null')
        }

        if (userPersonalInfo.value == null) {
            throw AppErrors.INSTANCE.fieldRequired('value').exception()
        }
        if (userPersonalInfo.type == null) {
            throw AppErrors.INSTANCE.fieldRequired('type').exception()
        }
        if (userPersonalInfo.label != null) {
            if (userPersonalInfo.label.length() > maxLabelLength) {
                throw AppErrors.INSTANCE.fieldTooLong('label', maxLabelLength).exception()
            }
            if (userPersonalInfo.label.length() < minLabelLength) {
                throw AppErrors.INSTANCE.fieldTooShort('label', minLabelLength).exception()
            }
        }
        if (userPersonalInfo.lastValidateTime != null) {
            if (userPersonalInfo.lastValidateTime.after(new Date())) {
                throw AppErrors.INSTANCE.fieldInvalid('lastValidateTime').exception()
            }
        }

        // todo:    Refactor validator using factory method
        if (userPersonalInfo.type == UserPersonalInfoType.ADDRESS.toString()) {
            Address address = ObjectMapperProvider.instance().treeToValue(
                    userPersonalInfo.value, Address)
            addressValidator.validate(address)
        } else if (userPersonalInfo.type == UserPersonalInfoType.DOB.toString()) {
            UserDOB dob = ObjectMapperProvider.instance().treeToValue(
                    userPersonalInfo.value, UserDOB)
            birthdayValidator.validate(dob)
        } else if (userPersonalInfo.type == UserPersonalInfoType.DRIVERS_LICENSE.toString()) {
            UserDriverLicense driverLicense = ObjectMapperProvider.instance().treeToValue(
                    userPersonalInfo.value, UserDriverLicense)
            driverLicenseValidator.validate(driverLicense)
        } else if (userPersonalInfo.type == UserPersonalInfoType.EMAIL.toString()) {
            Email email = ObjectMapperProvider.instance().treeToValue(
                    userPersonalInfo.value, Email)
            userEmailValidator.validate(email)
        } else if (userPersonalInfo.type == UserPersonalInfoType.GENDER.toString()) {
            UserGender userGender = ObjectMapperProvider.instance().treeToValue(
                    userPersonalInfo.value, UserGender)
            genderValidator.validate(userGender)
        } else if (userPersonalInfo.type == UserPersonalInfoType.GOVERNMENT_ID.toString()) {
            UserGovernmentID userGovernmentID = ObjectMapperProvider.instance().treeToValue(
                    userPersonalInfo.value, UserGovernmentID)
            governmentIDValidator.validate(userGovernmentID)
        } else if (userPersonalInfo.type == UserPersonalInfoType.NAME.toString()) {
            UserName userName = ObjectMapperProvider.instance().treeToValue(
                    userPersonalInfo.value, UserName)
            nameValidator.validateName(userName)
        } else if (userPersonalInfo.type == UserPersonalInfoType.PASSPORT.toString()) {
            UserPassport userPassport = ObjectMapperProvider.instance().treeToValue(
                    userPersonalInfo.value, UserPassport)
            passportValidator.validate(userPassport)
        } else if (userPersonalInfo.type == UserPersonalInfoType.PHONE.toString()) {
            PhoneNumber phoneNumber = ObjectMapperProvider.instance().treeToValue(
                    userPersonalInfo.value, PhoneNumber)
            userPhoneNumberValidator.validate(phoneNumber)
        } else if (userPersonalInfo.type == UserPersonalInfoType.QQ.toString()) {
            UserQQ qq = ObjectMapperProvider.instance().treeToValue(
                    userPersonalInfo.value, UserQQ)
            qqValidator.validate(qq)
        } else if (userPersonalInfo.type == UserPersonalInfoType.SMS.toString()) {
            UserSMS sms = ObjectMapperProvider.instance().treeToValue(
                    userPersonalInfo.value, UserSMS)
            smsValidator.validate(sms)
        } else if (userPersonalInfo.type == UserPersonalInfoType.WHATSAPP.toString()) {
            UserWhatsApp userWhatsApp = ObjectMapperProvider.instance().treeToValue(
                    userPersonalInfo.value, UserWhatsApp)
            userWhatsAppValidator.validate(userWhatsApp)
        } else {
            throw AppErrors.INSTANCE.fieldInvalid('type', UserPersonalInfoType.values().join(',')).exception()
        }
    }

    @Required
    void setAddressValidator(AddressValidator addressValidator) {
        this.addressValidator = addressValidator
    }

    @Required
    void setUserPersonalInfoRepository(UserPersonalInfoRepository userPersonalInfoRepository) {
        this.userPersonalInfoRepository = userPersonalInfoRepository
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
    void setNameValidator(NameValidator nameValidator) {
        this.nameValidator = nameValidator
    }

    @Required
    void setBirthdayValidator(BirthdayValidator birthdayValidator) {
        this.birthdayValidator = birthdayValidator
    }

    @Required
    void setSmsValidator(SMSValidator smsValidator) {
        this.smsValidator = smsValidator
    }

    @Required
    void setQqValidator(QQValidator qqValidator) {
        this.qqValidator = qqValidator
    }

    @Required
    void setPassportValidator(PassportValidator passportValidator) {
        this.passportValidator = passportValidator
    }

    @Required
    void setGovernmentIDValidator(GovernmentIDValidator governmentIDValidator) {
        this.governmentIDValidator = governmentIDValidator
    }

    @Required
    void setDriverLicenseValidator(DriverLicenseValidator driverLicenseValidator) {
        this.driverLicenseValidator = driverLicenseValidator
    }

    @Required
    void setGenderValidator(GenderValidator genderValidator) {
        this.genderValidator = genderValidator
    }

    @Required
    void setUserWhatsAppValidator(UserWhatsAppValidator userWhatsAppValidator) {
        this.userWhatsAppValidator = userWhatsAppValidator
    }

    @Required
    void setMinLabelLength(Integer minLabelLength) {
        this.minLabelLength = minLabelLength
    }

    @Required
    void setMaxLabelLength(Integer maxLabelLength) {
        this.maxLabelLength = maxLabelLength
    }
}
