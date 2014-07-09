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

    @ErrorDef(httpStatusCode = 409, code = "900001", message = "Csr log {0} not found.", field = "{0}")
    AppError csrLogNotFound(CsrLogId csrLogId);

    @ErrorDef(httpStatusCode = 409, code = "900002", message = "Invalid csr log search condition.")
    AppError invalidCsrLogSearch();

    @ErrorDef(httpStatusCode = 409, code = "900003", message = "Exceed csr log search range max limit days.")
    AppError exceedCsrLogSearchRange();

    @ErrorDef(httpStatusCode = 409, code = "900004", message = "Invalid country code.")
    AppError invalidCountryCode();

    @ErrorDef(httpStatusCode = 409, code = "900005", message = "User not found.")
    AppError userNotFound();

    @ErrorDef(httpStatusCode = 409, code = "900007", message = "Field {0} not writable.", field = "{0}")
    AppError fieldNotWritable(String field);

    @ErrorDef(httpStatusCode = 409, code = "900008", message = "Field {0} invalid. Allowed values: {1}",
            field = "{0}")
    AppError fieldInvalid(String field, String allowedValues);

    @ErrorDef(httpStatusCode = 409, code = "900009", message = "Field {0} invalid.", field = "{0}")
    AppError fieldInvalid(String field);

    @ErrorDef(httpStatusCode = 409, code = "900010", message = "Field {0} required.", field = "{0}")
    AppError fieldRequired(String field);

    @ErrorDef(httpStatusCode = 409, code = "900011", message = "Csr update {0} not found.", field = "{0}")
    AppError csrUpdateNotFound(CsrUpdateId csrUpdateId);

    @ErrorDef(httpStatusCode = 400, code = "900012", message = "Request body is empty")
    AppError requestBodyRequired();

    @ErrorDef(httpStatusCode = 400, code = "900013", message = "Date should be in ISO8601 format.(2014-07-08T10:41:15Z)")
    AppError dateFormatInvalid();
}
