/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.iap.spec.error;

import com.junbo.common.error.AppError;
import com.junbo.common.error.ErrorDef;
import com.junbo.common.error.ErrorProxy;

/**
 * AppErrors for iap.
 */
public interface AppErrors {
    AppErrors INSTANCE = ErrorProxy.newProxyInstance(AppErrors.class);

    @ErrorDef(httpStatusCode = 404, code = ErrorCode.ITEM_NOT_FOUND_WITH_PACKAGE_NAME,
            description = "Item could not be found with given packageName")
    AppError itemNotFoundWithPackageName();

    @ErrorDef(httpStatusCode = 409, code = ErrorCode.ENTITLEMENT_NOT_CONSUMABLE,
            description = "Entitlement not consumable, entitlementId={0}")
    AppError entitlementNotConsumable(String entitlementId);

    @ErrorDef(httpStatusCode = 409, code = ErrorCode.ENTITLEMENT_NOT_ENOUGH_USECOUNT,
            description = "Entitlement not enough use count, entitlementId={0}")
    AppError entitlementNotEnoughUsecount(String entitlementId);

    @ErrorDef(httpStatusCode = 409, code = ErrorCode.STORE_VALUE_PI_NOT_FOUND,
            description = "Store value PI not found")
    AppError storeValuePINotFound();
}
