/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.spec.error;

import com.junbo.common.error.AppError;
import com.junbo.common.error.ErrorDef;
import com.junbo.common.error.ErrorProxy;

/**
 * Created by lizwu on 3/11/14.
 */
public interface AppErrors {
    AppErrors INSTANCE = ErrorProxy.newProxyInstance(AppErrors.class);

    @ErrorDef(httpStatusCode = 404, code = "10001",
            description = "{0} is missing", field = "{0}")
    AppError missingConfiguration(String fieldName);

    @ErrorDef(httpStatusCode = 404, code = "10002", description = "Currency does not exist.")
    AppError currencyNotExist(String currency);

    @ErrorDef(httpStatusCode = 403, code = "10003",
            description = "Currency is not consistent: {0} & {1}.")
    AppError currencyNotConsistent(String currency, String other);

    @ErrorDef(httpStatusCode = 500, code = "10004", description = "Error occurred during calling Catalog service.")
    AppError catalogGatewayError();

    @ErrorDef(httpStatusCode = 500, code = "10005", description = "Error occurred during calling Entitlement service.")
    AppError entitlementGatewayError();

    @ErrorDef(httpStatusCode = 404, code = "10006",
            description = "No approved offerRevision is found for Offer: {0}")
    AppError offerRevisionNotFound(String offerId);

    @ErrorDef(httpStatusCode = 400, code = "10007",
            description = "Cannot purchase digital Offer: {0} with quantity of {1}.")
    AppError incorrectQuantity(String offerId, int quantity);

    @ErrorDef(httpStatusCode = 400, code = "10008", description = "Offer {0} is not purchasable in Country {1}")
    AppError offerNotPurchasable(String offerId, String country);
}
