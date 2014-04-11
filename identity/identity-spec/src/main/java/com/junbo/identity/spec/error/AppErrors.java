/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.error;

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

    @ErrorDef(httpStatusCode = 400, code = "10004", description = "Invalid input password. {0}")
    AppError invalidPassword(String errorMessage);

    @ErrorDef(httpStatusCode = 403, code = "10009", description = "Encrypt password error. {0}")
    AppError encryptPassword(String detail);

    @ErrorDef(httpStatusCode = 400, code = "10021", description = "Serializer error using jackson. source = {0}.")
    AppError serializerError(String source);

    @ErrorDef(httpStatusCode = 400, code = "10022", description = "Deserializer error using jackson.")
    AppError deSerializerError();

    @ErrorDef(httpStatusCode = 409, code = "2000001", description = "Field {0} not writable.", field = "{0}")
    AppError fieldNotWritable(String field);

    @ErrorDef(httpStatusCode = 409, code = "2000002", description = "Field {0} invalid. Allowed values: {1}",
            field = "{0}")
    AppError fieldInvalid(String field, String allowedValues);

    @ErrorDef(httpStatusCode = 409, code = "2000003", description = "Field {0} invalid.", field = "{0}")
    AppError fieldInvalid(String field);

    @ErrorDef(httpStatusCode = 409, code = "2000004", description = "Field {0} too short. Min length is {1}",
            field = "{0}")
    AppError fieldTooShort(String field, Integer minLength);

    @ErrorDef(httpStatusCode = 409, code = "2000005", description = "Field {0} too long. Max length is {1}",
            field = "{0}")
    AppError fieldTooLong(String field, Integer maxLength);

    @ErrorDef(httpStatusCode = 409, code = "2000006", description = "Field {0} duplicate.", field = "{0}")
    AppError fieldDuplicate(String field);

    @ErrorDef(httpStatusCode = 400, code = "2000007", description = "Field {0} required.", field = "{0}")
    AppError fieldRequired(String field);

    @ErrorDef(httpStatusCode = 404, code = "2000008", description = "User {0} not found.", field = "{0}")
    AppError userNotFound(UserId userId);

    @ErrorDef(httpStatusCode = 404, code = "2000009", description = "Group {0} not found.", field = "{0}")
    AppError groupNotFound(GroupId groupId);

    @ErrorDef(httpStatusCode = 409, code = "2000010", description = "Param name {0} required.", field = "{0}")
    AppError parameterRequired(String paraName);

    @ErrorDef(httpStatusCode = 409, code = "2000011", description = "Security Question {0} not found.", field = "{0}")
    AppError securityQuestionNotFound(SecurityQuestionId securityQuestionId);

    @ErrorDef(httpStatusCode = 409, code = "2000012", description = "User Authenticator {0} not found.", field = "{0}")
    AppError userAuthenticatorNotFound(UserAuthenticatorId userAuthenticatorId);

    @ErrorDef(httpStatusCode = 409, code = "2000013", description = "Param invalid due to {0}.", field = "{0}")
    AppError parameterInvalid(String message);

    @ErrorDef(httpStatusCode = 409, code = "2000014", description = "User Device {0} not found.", field = "{0}")
    AppError userDeviceNotFound(UserDeviceId userDeviceId);

    @ErrorDef(httpStatusCode = 409, code = "2000015", description = "User Group {0} not found.", field = "{0}")
    AppError userGroupNotFound(UserGroupId userGroupId);

    @ErrorDef(httpStatusCode = 409, code = "2000016", description = "User Login Attempt {0} not found.", field = "{0}")
    AppError userLoginAttemptNotFound(UserCredentialVerifyAttemptId userLoginAttemptId);

    @ErrorDef(httpStatusCode = 404, code = "2000017", description = "User {0} is not in valid status.", field = "{0}")
    AppError userInInvalidStatus(UserId userId);

    @ErrorDef(httpStatusCode = 404, code = "2000018", description = "Username {0} not found.", field = "{0}")
    AppError userNotFound(String username);

    @ErrorDef(httpStatusCode = 404, code = "2000019", description = "User password is incorrect.")
    AppError userPasswordIncorrect();

    @ErrorDef(httpStatusCode = 404, code = "2000020", description = "User pin is incorrect.")
    AppError userPinIncorrect();

    @ErrorDef(httpStatusCode = 409, code = "2000021", description = "User Tos {0} not found.", field = "{0}")
    AppError userTosNotFound(UserTosId userTosId);

    @ErrorDef(httpStatusCode = 409, code = "2000022", description = "User Optin {0} not found.", field = "{0}")
    AppError userOptinNotFound(UserOptinId userOptinId);

    @ErrorDef(httpStatusCode = 409, code = "2000023", description = "User Password {0} not found.", field = "{0}")
    AppError userPasswordNotFound(UserPasswordId userPasswordId);

    @ErrorDef(httpStatusCode = 409, code = "2000024", description = "User PhoneNumber {0} not found.", field = "{0}")
    AppError userPhoneNumberNotFound(UserPhoneNumberId userPhoneNumberId);

    @ErrorDef(httpStatusCode = 409, code = "2000025", description = "User Pin {0} not found.", field = "{0}")
    AppError userPinNotFound(UserPinId userPinId);

    @ErrorDef(httpStatusCode = 409, code = "2000026",
            description = "User Security Question {0} not found.", field = "{0}")
    AppError userSecurityQuestionNotFound(UserSecurityQuestionId userSecurityQuestionId);

    @ErrorDef(httpStatusCode = 409, code = "2000027", description = "Security Question {0} not found.", field = "{0}")
    AppError securityQuestionNotActive(SecurityQuestionId securityQuestionId);

    @ErrorDef(httpStatusCode = 409, code = "2000028",
            description = "User Security Question Attempt {0} not found.", field = "{0}")
    AppError userSecurityQuestionAttemptNotFound(
            UserSecurityQuestionVerifyAttemptId userSecurityQuestionVerifyAttemptId);

    @ErrorDef(httpStatusCode = 409, code = "2000029", description = "User Security Question isn't valid.")
    AppError userSecurityQuestionNotValid();

    @ErrorDef(httpStatusCode = 409, code = "2000030", description = "User Security Question not found.")
    AppError userSecurityQuestionNotFound();

    @ErrorDef(httpStatusCode = 404, code = "2000031", description = "User security question is incorrect.")
    AppError userSecurityQuestionIncorrect();

    @ErrorDef(httpStatusCode = 409, code = "2000032", description = "User Email {0} not found.", field = "{0}")
    AppError userEmailNotFound(UserEmailId userEmailId);

    @ErrorDef(httpStatusCode = 409, code = "2000033", description = "Device {0} not found.", field = "{0}")
    AppError deviceNotFound(DeviceId deviceId);

    @ErrorDef(httpStatusCode = 409, code = "2000034", description = "Tos {0} not found.", field = "{0}")
    AppError tosNotFound(TosId tosId);

    @ErrorDef(httpStatusCode = 404, code = "2000035", description = "Role {0} not found.", field = "{0}")
    AppError roleNotFound(RoleId roleId);

    @ErrorDef(httpStatusCode = 404, code = "2000036", description = "RoleAssignment {0} not found.", field = "{0}")
    AppError roleAssignmentNotFound(RoleAssignmentId roleAssignmentId);

    @ErrorDef(httpStatusCode = 404, code = "2000037", description = "User Pii {0} not found.", field = "{0}")
    AppError userPiiNotFound(UserPiiId userPiiId);
}
