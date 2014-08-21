/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.store.spec.error;

import com.junbo.common.error.AppError;
import com.junbo.common.error.ErrorDef;
import com.junbo.common.error.ErrorDetail;
import com.junbo.common.error.ErrorProxy;

/**
 * AppErrors for iap.
 */
public interface AppErrors {

    AppErrors INSTANCE = ErrorProxy.newProxyInstance(AppErrors.class);

    @ErrorDef(httpStatusCode = 412, code = "101", message = "Unknown Error.")
    AppError unknownError();

    @ErrorDef(httpStatusCode = 412, code = "102", message = "Entitlement Not Consumable.",
            field = "entitlementId", reason = "Entitlement not consumable, entitlementId={0}.")
    AppError entitlementNotConsumable(String entitlementId);

    @ErrorDef(httpStatusCode = 412, code = "103", message = "Entitlement Not Enough Use Count.",
            field = "entitlementId", reason = "Entitlement not enough use count, entitlementId={0}.")
    AppError entitlementNotEnoughUseCount(String entitlementId);

    @ErrorDef(httpStatusCode = 412, code = "104", message = "Stored Value Payment Instrument Not Found.")
    AppError storeValuePINotFound();

    @ErrorDef(httpStatusCode = 412, code = "105", message = "User Not Found By Username.")
    AppError userNotFoundByUsername();

    @ErrorDef(httpStatusCode = 412, code = "106", message = "Invalid billing update operation.")
    AppError invalidBillingUpdateOperation();

    @ErrorDef(httpStatusCode = 400, code = "107", message = "User Not Found.")
    AppError userNotFound();

    @ErrorDef(httpStatusCode = 400, code = "108", message = "Invalid Challenge Answer.")
    AppError invalidChallengeAnswer();

    @ErrorDef(httpStatusCode = 412, code = "107", message = "Invalid user status.")
    AppError invalidUserStatus();

    @ErrorDef(httpStatusCode = 412, code = "108", message = "Unsupported payment instrument type.")
    AppError unsupportedPaymentInstrumentType();

    @ErrorDef(httpStatusCode = 412, code = "109", message = "Invalid purchase token.")
    AppError invalidPurchaseToken(ErrorDetail[] details);

    @ErrorDef(httpStatusCode = 412, code = "109", message = "Invalid purchase token.")
    AppError invalidPurchaseToken();

    @ErrorDef(httpStatusCode = 412, code = "110", message = "Invalid offer: {0}")
    AppError invalidOffer(String reason);

    @ErrorDef(httpStatusCode = 412, code = "111", message = "Item Not Found With Package Name.")
    AppError itemNotFoundWithPackageName();
}
