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

    @ErrorDef(httpStatusCode = 412, code = "101", message = "Item Not Found With Package Name")
    AppError itemNotFoundWithPackageName();

    @ErrorDef(httpStatusCode = 412, code = "102", message = "Entitlement Not Consumable",
            field = "entitlementId", reason = "Entitlement not consumable, entitlementId={0}")
    AppError entitlementNotConsumable(String entitlementId);

    @ErrorDef(httpStatusCode = 412, code = "103", message = "Entitlement Not Enough Use Count",
            field = "entitlementId", reason = "Entitlement not enough use count, entitlementId={0}")
    AppError entitlementNotEnoughUsecount(String entitlementId);

    @ErrorDef(httpStatusCode = 412, code = "104", message = "Stored Value Payment Instrument Not Found")
    AppError storeValuePINotFound();
}
