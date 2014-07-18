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

    @ErrorDef(httpStatusCode = 412, code = "104", message = "Invalid Country Code")
    AppError invalidCountryCode();

    @ErrorDef(httpStatusCode = 412, code = "105", message = "User Not Found")
    AppError userNotFound();

    @ErrorDef(httpStatusCode = 412, code = "106", message = "Group Not Found")
    AppError groupNotFound();

    @ErrorDef(httpStatusCode = 412, code = "107", message = "Field Not Writable", field = "{0}", reason = "Field {0} is not writeable.")
    AppError fieldNotWritable(String field);

    @ErrorDef(httpStatusCode = 412, code = "108", message = "Field Invalid",  field = "{0}", reason = "Field {0} is invalid, allowed values: {1}.")
    AppError fieldInvalid(String field, String allowedValues);

    @ErrorDef(httpStatusCode = 412, code = "109", message = "Field invalid", field = "{0}", reason = "Field {0} is invalid.")
    AppError fieldInvalid(String field);

    @ErrorDef(httpStatusCode = 412, code = "110", message = "Field Required", field = "{0}")
    AppError fieldRequired(String field);

    @ErrorDef(httpStatusCode = 412, code = "111", message = "CSR Update Not Found", field = "id", reason = "CSR Update with id {0} is not found.")
    AppError csrUpdateNotFound(CsrUpdateId csrUpdateId);

    @ErrorDef(httpStatusCode = 412, code = "112", message = "Request body is required")
    AppError requestBodyRequired();

    @ErrorDef(httpStatusCode = 412, code = "113", message = "Date should be in ISO8601 format.(2014-07-08T10:41:15Z)")
    AppError dateFormatInvalid();

    @ErrorDef(httpStatusCode = 412, code = "114", message = "Invalid Request")
    AppError invalidRequest();

    @ErrorDef(httpStatusCode = 412, code = "115", message = "Get access Token failed.")
    AppError getAccessTokenFailed();

    @ErrorDef(httpStatusCode = 412, code = "116", message = "CSR group not found.")
    AppError csrGroupNotFound();

    @ErrorDef(httpStatusCode = 412, code = "117", message = "Pending csr group not found.")
    AppError pendingCsrGroupNotFound();

    @ErrorDef(httpStatusCode = 412, code = "118", message = "Email template not found.")
    AppError emailTemplateNotFound();

    @ErrorDef(httpStatusCode = 412, code = "119", message = "Send email failed.")
    AppError sendEmailFailed();

    @ErrorDef(httpStatusCode = 412, code = "120", message = "CSR Invitation Code is Missing")
    AppError csrInvitationCodeMissing();

    @ErrorDef(httpStatusCode = 412, code = "120", message = "CSR Invitation Code is Invalid")
    AppError csrInvitationCodeInvalid();
}
