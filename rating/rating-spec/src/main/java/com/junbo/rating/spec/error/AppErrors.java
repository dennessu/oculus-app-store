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

    @ErrorDef(httpStatusCode = 412, code = "101", message = "Missing Configuration.",
            field = "{0}", reason = "{0} is not configured in offer.")
    AppError missingConfiguration(String fieldName);

    @ErrorDef(httpStatusCode = 412, code = "102", message = "CurrencyInfo Not Found.",
            field = "currency", reason = "Currency with ID {0} is not found.")
    AppError invalidCurrencyCode(String currency);

    @ErrorDef(httpStatusCode = 412, code = "103", message = "Offer Revision Not Found.",
            field = "offerRevision", reason = "Approved Offer Revision is not found for offer {0}.")
    AppError offerRevisionNotFound(String offerId);

    @ErrorDef(httpStatusCode = 412, code = "104", message = "Incorrect Quantity.",
            field = "quantity", reason = "Cannot purchase digital Offer: {0} with quantity of {1}.")
    AppError incorrectQuantity(String offerId, int quantity);

    @ErrorDef(httpStatusCode = 412, code = "105", message = "Offer Not Purchasable.",
            field = "offer", reason = "Offer {0} is not purchasable in Country {1}.")
    AppError offerNotPurchasable(String offerId, String country);

    @ErrorDef(httpStatusCode = 500, code = "110", message = "Catalog Gateway Error.")
    AppError catalogGatewayError();

    @ErrorDef(httpStatusCode = 500, code = "111", message = "Entitlement Gateway Error.")
    AppError entitlementGatewayError();
}
