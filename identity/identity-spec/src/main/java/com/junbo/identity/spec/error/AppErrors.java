/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.error;

import com.junbo.common.enumid.CountryId;
import com.junbo.common.enumid.CurrencyId;
import com.junbo.common.enumid.DeviceTypeId;
import com.junbo.common.enumid.LocaleId;
import com.junbo.common.error.AppError;
import com.junbo.common.error.ErrorDef;
import com.junbo.common.error.ErrorProxy;
import com.junbo.common.id.*;

/**
 * Interface for AppError.
 * HttpStatusCode please refer to http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
 */

public interface AppErrors {
    AppErrors INSTANCE = ErrorProxy.newProxyInstance(AppErrors.class);

    @ErrorDef(httpStatusCode = 412, code = "101", message = "Invalid Password", field = "password", reason = "{0}")
    AppError invalidPassword(String errorMessage);

    @ErrorDef(httpStatusCode = 404, code = "102", message = "User Not Found",
            field = "user", reason = "User with ID {0} is not found")
    AppError userNotFound(UserId id);

    @ErrorDef(httpStatusCode = 404, code = "103", message = "Group Not Found",
            field = "group", reason = "Group with ID {0} is not found")
    AppError groupNotFound(GroupId groupId);

    @ErrorDef(httpStatusCode = 404, code = "104", message = "User Authenticator Not Found",
            field = "userAuthenticator", reason = "User Authenticator with ID {0} is not found")
    AppError userAuthenticatorNotFound(UserAuthenticatorId userAuthenticatorId);

    @ErrorDef(httpStatusCode = 404, code = "105", message = "User Group Not Found",
            field = "userGroup", reason = "User Group with ID {0} is not found")
    AppError userGroupNotFound(UserGroupId userGroupId);

    @ErrorDef(httpStatusCode = 404, code = "106", message = "User Login Attempt Not Found",
            field = "userLoginAttempt", reason = "User Login Attempt with ID {0} is not found")
    AppError userLoginAttemptNotFound(UserCredentialVerifyAttemptId userLoginAttemptId);

    @ErrorDef(httpStatusCode = 412, code = "107", message = "User Status Invalid",
            field = "user.status", reason = "User with ID {0} is not in valid status")
    AppError userInInvalidStatus(UserId userId);

    @ErrorDef(httpStatusCode = 412, code = "107", message = "User Status Invalid",
            field = "user.status", reason = "User with name {0} is not in valid status")
    AppError userInInvalidStatusByName(String userName);

    @ErrorDef(httpStatusCode = 404, code = "108", message = "User Name Not Found",
            field = "username", reason = "User with name {0} is not found")
    AppError userNotFoundByName(String username);

    @ErrorDef(httpStatusCode = 412, code = "109", message = "User Password Incorrect")
    AppError userPasswordIncorrect();

    @ErrorDef(httpStatusCode = 412, code = "110", message = "User Pin Incorrect")
    AppError userPinIncorrect();

    @ErrorDef(httpStatusCode = 404, code = "111", message = "User Tos Not Found",
            field = "userTosId", reason = "User Tos Agreement with ID {0} is not found")
    AppError userTosAgreementNotFound(UserTosAgreementId userTosId);

    @ErrorDef(httpStatusCode = 404, code = "112", message = "User Optin Not Found",
            field = "userOptinId", reason = "User Optin with ID {0} is not found")
    AppError userOptinNotFound(UserCommunicationId userOptinId);

    @ErrorDef(httpStatusCode = 404, code = "113", message = "User Password Not Found",
            field = "userPasswordId", reason = "User Password with ID {0} is not found")
    AppError userPasswordNotFound(UserPasswordId userPasswordId);

    @ErrorDef(httpStatusCode = 404, code = "114", message = "User Pin Not Found",
            field = "userPinId", reason = "User Pin with ID {0} is not found")
    AppError userPinNotFound(UserPinId userPinId);

    @ErrorDef(httpStatusCode = 404, code = "115", message = "User Security Question Not Found",
            field = "userSecurityQuestion", reason = "User Security Question with ID {0} is not found")
    AppError userSecurityQuestionNotFound(UserSecurityQuestionId userSecurityQuestionId);

    @ErrorDef(httpStatusCode = 404, code = "116", message = "User Security Question Attempt Not Found",
            field = "userSecurityQuestionAttempt", reason = "User Security Question Attempt with ID {0} is not found")
    AppError userSecurityQuestionAttemptNotFound(
            UserSecurityQuestionVerifyAttemptId userSecurityQuestionVerifyAttemptId);

    @ErrorDef(httpStatusCode = 412, code = "117", message = "User Security Question Incorrect")
    AppError userSecurityQuestionIncorrect();

    @ErrorDef(httpStatusCode = 404, code = "118", message = "Device Not Found",
            field = "device", reason = "Device with ID {0} is not found")
    AppError deviceNotFound(DeviceId deviceId);

    @ErrorDef(httpStatusCode = 404, code = "119", message = "Tos Not Found",
            field = "tos", reason = "Tos with ID {0} is not found")
    AppError tosNotFound(TosId tosId);

    @ErrorDef(httpStatusCode = 404, code = "120", message = "User Personal Info Not Found",
            field = "userPersonalInfo", reason = "User Personal Info with ID {0} is not found")
    AppError userPersonalInfoNotFound(UserPersonalInfoId userPersonalInfoId);

    @ErrorDef(httpStatusCode = 404, code = "121", message = "User TFA Not Found",
            field = "userTFA", reason = "User TFA with ID {0} is not found")
    AppError userTFANotFound(UserTFAId userTFAId);

    @ErrorDef(httpStatusCode = 404, code = "122", message = "Country Not Found",
            field = "country", reason = "Country with ID {0} is not found")
    AppError countryNotFound(CountryId countryId);

    @ErrorDef(httpStatusCode = 404, code = "123", message = "Currency Not Found",
            field = "currency", reason = "Currency with ID {0} is not found")
    AppError currencyNotFound(CurrencyId countryId);

    @ErrorDef(httpStatusCode = 404, code = "124", message = "Locale Not Found",
            field = "locale", reason = "Locale with ID {0} is not found")
    AppError localeNotFound(LocaleId localeId);

    @ErrorDef(httpStatusCode = 404, code = "125", message = "Payment Instrument Type Not Found",
            field = "paymentInstrumentType", reason = "Payment Instrument Type with ID {0} is not found")
    AppError piTypeNotFound(PITypeId piTypeId);

    @ErrorDef(httpStatusCode = 404, code = "126", message = "Communication Not Found",
            field = "communication", reason = "Communication with ID {0} is not found")
    AppError communicationNotFound(CommunicationId id);

    @ErrorDef(httpStatusCode = 404, code = "127", message = "Device Type Not Found",
            field = "deviceType", reason = "Device Type with ID {0} is not found")
    AppError deviceTypeNotFound(DeviceTypeId id);

    @ErrorDef(httpStatusCode = 404, code = "128", message = "User TFA Attempt Not Found",
            field = "userTFAAttempt", reason = "User TFA Attempt with ID {0} is not found")
    AppError userTFAAttemptNotFound(UserTFAAttemptId userTFAAttemptId);

    @ErrorDef(httpStatusCode = 412, code = "129", message = "User TFA Code Incorrect")
    AppError userTFACodeIncorrect();

    @ErrorDef(httpStatusCode = 404, code = "130", message = "User TFA Backup Code Not Found",
            field = "userTFABackupCode", reason = "User TFA Backup Code with ID {0} is not found")
    AppError userTFABackupCodeNotFound(UserTFABackupCodeId userTFABackupCodeId);

    @ErrorDef(httpStatusCode = 404, code = "131", message = "User TFA Backup Code Attempt Not Found",
            field = "userTFABackupCodeAttempt", reason = "User TFA Backup Code Attempt with ID {0} is not found")
    AppError userTFABackupCodeAttemptNotFound(UserTFABackupCodeAttemptId userTFABackupCodeAttemptId);

    @ErrorDef(httpStatusCode = 412, code = "132", message = "User TFA Backup Code Incorrect")
    AppError userTFABackupCodeIncorrect();

    @ErrorDef(httpStatusCode = 404, code = "133", message = "Organization Not Found",
            field = "organization", reason = "Organization with ID {0} is not found")
    AppError organizationNotFound(OrganizationId organizationId);

    @ErrorDef(httpStatusCode = 412, code = "134", message = "TeleSign Provider Error",
            field = "cause", reason = "{0}")
    AppError teleSignProviderError(String message);

    @ErrorDef(httpStatusCode = 409, code = "135", message = "Email Already Exists",
            field = "email", reason = "Email {0} is already used")
    AppError userEmailAlreadyUsed(String email);

    @ErrorDef(httpStatusCode = 409, code = "136", message = "Organization Name Already Exists",
            field = "organization.name", reason = "Organization name {0} is already used")
    AppError organizationAlreadyUsed(String organizationName);

    @ErrorDef(httpStatusCode = 404, code = "137", message = "PaymentInstrument Not Found",
            field = "paymentInstrument", reason = "PaymentInstrument with ID {0} is not found")
    AppError paymentInstrumentNotFound(PaymentInstrumentId id);

    @ErrorDef(httpStatusCode = 404, code = "138", message = "ErrorInfo Not Found",
            field = "errorIdentifier", reason = "ErrorInfo with ID {0} is not found")
    AppError errorInfoNotFound(ErrorIdentifier errorIdentifier);

    @ErrorDef(httpStatusCode = 429, code = "139", message = "User reaches maximum login attempts",
            field = "username", reason = "User reaches maximum login attempts")
    AppError maximumLoginAttempt();

    @ErrorDef(httpStatusCode = 412, code = "140", message = "Age restriction error",
            field = "dob", reason = "Age restriction error")
    AppError ageRestrictionError();

    @ErrorDef(httpStatusCode = 412, code = "141", message = "User reaches maximum same user attempt retry count")
    AppError maximumSameUserAttempt();

    @ErrorDef(httpStatusCode = 412, code = "142", message = "User reaches maximum same IP attempt retry count")
    AppError maximumSameIPAttempt();
}
