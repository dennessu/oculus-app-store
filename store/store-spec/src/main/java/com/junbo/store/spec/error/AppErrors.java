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

    @ErrorDef(httpStatusCode = 500, code = "101", message = "Unknown Error.")
    AppError unknownError();

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

    @ErrorDef(httpStatusCode = 412, code = "112", message = "User Primary Email Not Verified.")
    AppError userPrimaryEmailNotVerified();

    @ErrorDef(httpStatusCode = 412, code = "113", message = "User Email Not found.")
    AppError userEmailNotFound();

    @ErrorDef(httpStatusCode = 412, code = "114", message = "User Primary Email Not found.")
    AppError userEmailPrimaryEmailNotFound();

    @ErrorDef(httpStatusCode = 412, code = "115", message = "User Reaches Maximum Attempt Count.")
    AppError maximumAttemptReached();

    @ErrorDef(httpStatusCode = 412, code = "116", message = "Invalid Update Profile Token.")
    AppError invalidUpdateProfileToken();

    @ErrorDef(httpStatusCode = 412, code = "117", message = "Section Not Found.")
    AppError sectionNotFound();

    @ErrorDef(httpStatusCode = 412, code = "118", message = "Item Version Code Not Found.")
    AppError itemVersionCodeNotFound();

    @ErrorDef(httpStatusCode = 412, code = "119", message = "Review already exists.")
    AppError reviewAlreadyExists();

    @ErrorDef(httpStatusCode = 412, code = "120", message = "item not purchased.")
    AppError itemNotPurchased();

    @ErrorDef(httpStatusCode = 412, code = "121", message = "Sentry block register access.")
    AppError sentryBlockRegisterAccess();

    @ErrorDef(httpStatusCode = 412, code = "122", message = "Sentry block login access.")
    AppError sentryBlockLoginAccess();

    @ErrorDef(httpStatusCode = 412, code = "123", message = "Register TOS Not Found.")
    AppError RegisterTosNotFound();

    @ErrorDef(httpStatusCode = 412, code = "124", message = "Error occurs, please retry again.")
    AppError retryableError();

    @ErrorDef(httpStatusCode = 412, code = "125", message = "Sentry block check email.")
    AppError sentryBlockCheckEmail();

    @ErrorDef(httpStatusCode = 412, code = "126", message = "Sentry block check username.")
    AppError sentryBlockCheckUsername();

    @ErrorDef(httpStatusCode = 412, code = "127", message = "Unsupported Country.")
    AppError unsupportedCountry();

    @ErrorDef(httpStatusCode = 412, code = "150", message = "IAP purchase not consumable.")
    AppError iapPurchaseNotConsumable();

    @ErrorDef(httpStatusCode = 412, code = "151", message = "Invalid IAP purchase token.",
            field = "iapPurchaseToken", reason = "Invalid IAP purchase token.")
    AppError invalidIAPPurchaseToken();

    @ErrorDef(httpStatusCode = 412, code = "152", message = "IAP item not found via sku.")
    AppError iapItemNotFoundWithSku();
}
