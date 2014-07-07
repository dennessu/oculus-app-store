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

    @ErrorDef(httpStatusCode = 409, code = "900001", description = "Csr log {0} not found.", field = "{0}")
    AppError csrLogNotFound(CsrLogId csrLogId);

    @ErrorDef(httpStatusCode = 409, code = "900002", description = "Invalid csr log search condition.")
    AppError invalidCsrLogSearch();

    @ErrorDef(httpStatusCode = 409, code = "900003", description = "Exceed csr log search range max limit days.")
    AppError exceedCsrLogSearchRange();

    @ErrorDef(httpStatusCode = 409, code = "900004", description = "Invalid country code.")
    AppError invalidCountryCode();

    @ErrorDef(httpStatusCode = 409, code = "900005", description = "User not found.")
    AppError userNotFound();

    @ErrorDef(httpStatusCode = 409, code = "900007", description = "Field {0} not writable.", field = "{0}")
    AppError fieldNotWritable(String field);

    @ErrorDef(httpStatusCode = 409, code = "900008", description = "Field {0} invalid. Allowed values: {1}",
            field = "{0}")
    AppError fieldInvalid(String field, String allowedValues);

    @ErrorDef(httpStatusCode = 409, code = "900009", description = "Field {0} invalid.", field = "{0}")
    AppError fieldInvalid(String field);

    @ErrorDef(httpStatusCode = 409, code = "900010", description = "Field {0} required.", field = "{0}")
    AppError fieldRequired(String field);

    @ErrorDef(httpStatusCode = 409, code = "900010", description = "Csr update {0} not found.", field = "{0}")
    AppError csrUpdateNotFound(CsrUpdateId csrUpdateId);
}
