/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.csr.spec.error;

import com.junbo.common.error.AppError;
import com.junbo.common.error.ErrorDef;
import com.junbo.common.error.ErrorProxy;
import com.junbo.common.id.CsrLogId;
import com.junbo.common.id.CsrUpdateId;
import com.junbo.common.id.GroupId;
import com.junbo.common.id.UserId;

/**
 * AppErrors.
 */
public interface AppErrors {
    AppErrors INSTANCE = ErrorProxy.newProxyInstance(AppErrors.class);

    @ErrorDef(httpStatusCode = 412, code = "101", message = "CSR Log Not Found", field = "id", reason = "CSR Log with id {0} is not found.")
    AppError csrLogNotFound(CsrLogId csrLogId);

    @ErrorDef(httpStatusCode = 412, code = "102", message = "Invalid CSR Log Search")
    AppError invalidCsrLogSearch();

    @ErrorDef(httpStatusCode = 412, code = "103", message = "Exceed CSR Log Search Range")
    AppError exceedCsrLogSearchRange();

    @ErrorDef(httpStatusCode = 412, code = "106", message = "Group Not Found By Id", reason = "Group not found with id {0}")
    AppError groupNotFoundById(GroupId groupId);

    @ErrorDef(httpStatusCode = 412, code = "111", message = "CSR Update Not Found", field = "id", reason = "CSR Update with id {0} is not found.")
    AppError csrUpdateNotFound(CsrUpdateId csrUpdateId);

    @ErrorDef(httpStatusCode = 412, code = "112", message = "Request Body Required")
    AppError requestBodyRequired();

    @ErrorDef(httpStatusCode = 412, code = "113", message = "Date Format Invalid", reason = "Date should be in ISO8601 format.(2014-07-08T10:41:15Z)")
    AppError dateFormatInvalid();

    @ErrorDef(httpStatusCode = 412, code = "114", message = "Invalid Request")
    AppError invalidRequest();

    @ErrorDef(httpStatusCode = 412, code = "115", message = "Get Access Token Failed")
    AppError getAccessTokenFailed();

    @ErrorDef(httpStatusCode = 412, code = "116", message = "CSR Group Not Loaded")
    AppError csrGroupNotLoaded();

    @ErrorDef(httpStatusCode = 412, code = "117", message = "Pending CSR Group Not Found")
    AppError pendingCsrGroupNotFound();

    @ErrorDef(httpStatusCode = 412, code = "118", message = "Email template Not Found")
    AppError emailTemplateNotFound();

    @ErrorDef(httpStatusCode = 412, code = "119", message = "Send CSR Invitation Email Failed")
    AppError sendCSRInvitationEmailFailed();

    @ErrorDef(httpStatusCode = 412, code = "120", message = "CSR Invitation Code Missing")
    AppError csrInvitationCodeMissing();

    @ErrorDef(httpStatusCode = 412, code = "121", message = "CSR Invitation Code Invalid")
    AppError csrInvitationCodeInvalid();

    @ErrorDef(httpStatusCode = 412, code = "122", message = "User Not Found By Id", reason = "User not found with id {0}")
    AppError userNotFoundById(UserId userId);

    @ErrorDef(httpStatusCode = 412, code = "123", message = "User Not Found By Email", reason = "User not found with email {0}")
    AppError userNotFoundByEmail(String email);

    @ErrorDef(httpStatusCode = 412, code = "124", message = "User Not Found By Username", reason = "User not found with username {0}")
    AppError userNotFoundByUsername(String username);

    @ErrorDef(httpStatusCode = 412, code = "125", message = "Organization Not Found", reason = "Validate organization not found with orgName {0}")
    AppError organizationNotFound(String organizationName);

    @ErrorDef(httpStatusCode = 412, code = "126", message = "Invalid Email")
    AppError invalidEmail();

    @ErrorDef(httpStatusCode = 412, code = "127", message = "User Already In CSR Group", reason = "User {0} already in CSR group")
    AppError userAlreadyInCsrGroup(String user);
}
