/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.drm.spec.error;

import com.junbo.common.error.AppError;
import com.junbo.common.error.ErrorDef;
import com.junbo.common.error.ErrorProxy;
import com.junbo.common.id.ItemId;
import com.junbo.common.id.UserId;

/**
 * AppErrors.
 */
public interface AppErrors {
    AppErrors INTSTANCE = ErrorProxy.newProxyInstance(AppErrors.class);

    @ErrorDef(httpStatusCode = 412, code = "101", message = "Invalid item type",
            field = "itemType", reason = "The itemType {0} is invalid")
    AppError invalidItemType(String itemType);

    @ErrorDef(httpStatusCode = 412, code = "102", message = "User Not Found",
            field = "userId", reason = "User with ID {1} is not found")
    AppError userNotFound(UserId userId);

    @ErrorDef(httpStatusCode = 403, code = "103", message = "User does not have DOWNLOAD entitlement",
            reason = "User with ID {0} does not have DOWNLOAD entitlement for ItemId {1}")
    AppError noDownloadEntitlement(UserId userId, ItemId itemId);

    @ErrorDef(httpStatusCode = 500, code = "104", message = "Signature error",
            reason = "Exception happens during signing the response.")
    AppError signatureError();
}
