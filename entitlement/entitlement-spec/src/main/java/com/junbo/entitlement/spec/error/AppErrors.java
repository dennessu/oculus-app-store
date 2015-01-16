/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.spec.error;

import com.junbo.common.error.AppError;
import com.junbo.common.error.ErrorDef;
import com.junbo.common.error.ErrorProxy;
import com.junbo.common.id.UserId;

/**
 * Interface for AppError.
 * Copied from identity.
 */

public interface AppErrors {
    com.junbo.entitlement.spec.error.AppErrors INSTANCE =
            ErrorProxy.newProxyInstance(com.junbo.entitlement.spec.error.AppErrors.class);

    @ErrorDef(httpStatusCode = 412, code = "101", message = "User Not Found",
            field = "{0}", reason = "User with ID {1} is not found")
    AppError userNotFound(String field, UserId id);

    @ErrorDef(httpStatusCode = 412, code = "102", message = "Item Not Found",
            field = "{0}", reason = "Item with ID {1} is not found")
    AppError itemNotFound(String field, String itemId);

    @ErrorDef(httpStatusCode = 412, code = "103", message = "Entitlement Not Transferable",
            field = "cause", reason = "Entitlement {0} cannot be transferred. {1}")
    AppError entitlementNotTransferable(Object id, String msg);

    @ErrorDef(httpStatusCode = 412, code = "104", message = "Error Parsing Download URL",
            field = "cause", reason = "{0}")
    AppError errorParsingDownloadUrl(String msg);
}
