/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.error;

/**
 * Interface for CommonErrors.
 * HttpStatusCode please refer to http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
 */

public interface CommonErrors {
    CommonErrors INSTANCE = ErrorProxy.newProxyInstance(CommonErrors.class);

    @ErrorDef(httpStatusCode = 400, code = "000.001", description = "Invalid identifier: {0}.")
    AppError invalidId(String id);

    @ErrorDef(httpStatusCode = 400, code = "000.002", description = "Resource rev is outdated. id: {0} rev: {1}")
    AppError updateConflict(String id, String rev);
}
