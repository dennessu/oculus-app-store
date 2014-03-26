/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.error;

import com.junbo.common.error.*;
import com.junbo.common.id.UserId;

/**
 * Interface for AppError.
 * HttpStatusCode please refer to http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
 */

public interface AppErrors {
    AppErrors INSTANCE = ErrorProxy.newProxyInstance(AppErrors.class);

    @ErrorDef(httpStatusCode = 400, code = "10000", description = "Invalid parameter: {0}.")
    AppError invalidParameter(String param);

    // Redefined, the below will all retired
    @ErrorDef(httpStatusCode = 400, code = "10000", description ="Invalid null/empty input parameter")
    AppError invalidNullEmptyInputParam();

    @ErrorDef(httpStatusCode = 403, code = "10001",
            description = "Object type doesn't map. actually: {0}, expected: {1}.")
    AppError invalidObjectType(Class actually, Class expected);

    @ErrorDef(httpStatusCode = 400, code = "10002", description = "Missing Input field. field: {0}")
    AppError missingParameterField(String field);

    @ErrorDef(httpStatusCode = 400, code = "10003", description = "Unnecessary field found. field: {0}")
    AppError unnecessaryParameterField(String field);

    @ErrorDef(httpStatusCode = 400, code = "10004", description = "Invalid input password. {0}")
    AppError invalidPassword(String errorMessage);

    @ErrorDef(httpStatusCode = 403, code = "10005",
            description = "Object value doesn't match. actually: {0}, expected: {1}.")
    AppError unmatchedValue(String actually, String expected);

    @ErrorDef(httpStatusCode = 403, code = "10006", description = "User {0} is already exists.")
    AppError userAlreadyExists(String userName);

    @ErrorDef(httpStatusCode = 403, code = "10007", description = "User {0} is already banned.")
    AppError userAlreadyBanned(String userName);

    @ErrorDef(httpStatusCode = 403, code = "10008", description = "User {0} is pending for confirmation.")
    AppError userPendingForConfirmation(String userName);

    @ErrorDef(httpStatusCode = 403, code = "10009", description = "Encrypt password error. {0}")
    AppError encryptPassword(String detail);

    @ErrorDef(httpStatusCode = 400, code = "10010",
            description = "Input parameters doesn't match. Field1: {0}, Field2: {1}")
    AppError inputParametersMismatch(String field1, String field2);

    @ErrorDef(httpStatusCode = 400, code = "10011", description = "Invalid Resource get request.")
    AppError invalidResourceRequest();

    @ErrorDef(httpStatusCode = 403, code = "10012", description = "User {0} doesn't exist.")
    AppError notExistingUser(String detail);

    @ErrorDef(httpStatusCode = 403, code = "10013", description = "User {0} status error.")
    AppError userStatusError(String detail);

    @ErrorDef(httpStatusCode = 400, code = "10014", description = "User {0} has wrong password.")
    AppError userNamePasswordNotMatch(String userName);

    @ErrorDef(httpStatusCode = 403, code = "10015", description = "Can't update password to weaker level.")
    AppError updatePasswordToWeaker();

    @ErrorDef(httpStatusCode = 403, code = "10016", description = "User {0} with deviceProfileId {1} doesn't exist.")
    AppError notExistingUserDeviceProfile(String userId, String deviceProfileId);

    @ErrorDef(httpStatusCode = 403, code = "10017",
            description = "User {0} with federationId {1} doesn't exist.")
    AppError notExistingUserFederation(String userId, String federationId);

    @ErrorDef(httpStatusCode = 403, code = "10018", description = "User {0} with userOptInId {1} doesn't exist.")
    AppError notExistingUserOptIn(String userId, String userOptInId);

    @ErrorDef(httpStatusCode = 403, code = "10019",
            description = "User {0} with userTosId {1} doesn't exist.")
    AppError notExistingUserTosAcceptance(String userId, String userTosId);

    @ErrorDef(httpStatusCode = 403, code = "10020", description = "Duplicate password rule details.")
    AppError duplicatePasswordRuleDetails();

    @ErrorDef(httpStatusCode = 400, code = "10021", description = "Serializer error using jackson. source = {0}.")
    AppError serializerError(String source);

    @ErrorDef(httpStatusCode = 400, code = "10022", description = "Deserializer error using jackson.")
    AppError deSerializerError();

    @ErrorDef(httpStatusCode = 403, code = "10023", description = "Invalid User status. detail = {0}")
    AppError invalidUserStatus(String detail);

    @ErrorDef(httpStatusCode = 400, code = "10024", description = "User reached maximum failure retry count.")
    AppError maximumFailureRetryCountReached();

    @ErrorDef(httpStatusCode = 400, code = "10025", description = "User tos {0} has been accepted before.")
    AppError userTosAlreadyAccepted(String tos);

    @ErrorDef(httpStatusCode = 400, code = "10025", description = "User profile with type {0} already exists.")
    AppError userProfileAlreadyExists(String profileType);

    @ErrorDef(httpStatusCode = 400, code = "10026", description = "User Optin with type {0} already exists.")
    AppError userOptinAlreadyExists(String optInType);

    @ErrorDef(httpStatusCode = 400, code = "10027", description = "User Federation with type {0} already exists.")
    AppError userFederationAlreadyExists(String federationType);

    @ErrorDef(httpStatusCode = 400, code = "10028", description = "User with type {0} and device {1} already exists.")
    AppError userDeviceProfileAlreadyExists(String type, String deviceId);

    @ErrorDef(httpStatusCode = 400, code = "10029", description = "Enum value {0} not exists in type {1}")
    AppError enumConversionError(String enumValue, String enumType);

    @ErrorDef(httpStatusCode = 400, code = "10030", description = "Invalid request.")
    AppError invalidRequest();




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

    @ErrorDef(httpStatusCode = 409, code = "2000007", description = "Field {0} required.", field = "{0}")
    AppError fieldRequired(String field);

    @ErrorDef(httpStatusCode = 404, code = "2000007", description = "User {0} not found.")
    AppError userNotFound(UserId userId);
}
