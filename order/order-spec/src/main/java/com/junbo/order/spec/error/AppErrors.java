/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.error;

import com.junbo.common.error.AppError;
import com.junbo.common.error.ErrorDef;
import com.junbo.common.error.ErrorProxy;

/**
 * Interface for AppError.
 * HttpStatusCode please refer to http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
 */

public interface AppErrors {
    AppErrors INSTANCE = ErrorProxy.newProxyInstance(AppErrors.class);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.INVALID_NULL_EMPTY_INPUT_PARAM,
            description ="Invalid null/empty input parameter")
    AppError invalidNullEmptyInputParam();

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.INVALID_OBJECT_TYPE,
            description = "Object type doesn't map. actually: {0}, expected: {1}.")
    AppError invalidObjectType(Class actually, Class expected);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.MISSING_PARAMETER_FIELD,
            description = "Missing Input field. field: {0}")
    AppError missingParameterField(String field);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.UNNECESSARY_PARAMETER_FIELD,
            description = "Unnecessary field found. field: {0}")
    AppError unnecessaryParameterField(String field);

    @ErrorDef(httpStatusCode = 404, code = ErrorCode.ORDER_NOT_FOUND,
            description = "Order not found")
    AppError orderNotFound();

    @ErrorDef(httpStatusCode = 500, code = ErrorCode.ORDER_TYPE_NOT_SUPPORTED,
            description = "Order action {0} is not supported")
    AppError orderTypeNotSupported(String type);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.ORDER_ACTION_NOT_SUPPORTED,
            description = "Order type {0} is not supported")
    AppError orderActionNotSupported(String action);

    @ErrorDef(httpStatusCode = 404, code = ErrorCode.ORDER_ITEM_NOT_FOUND,
            description = "Order item not found")
    AppError orderItemNotFound();

    @ErrorDef(httpStatusCode = 409, code = ErrorCode.ORDER_NOT_TENTATIVE,
            description = "Order not tentative")
    AppError orderNotTentative();

    @ErrorDef(httpStatusCode = 409, code = ErrorCode.DUPLICATE_TRACKING_GUID,
            description = "Order duplicate tracking GUID")
    AppError orderDuplicateTrackingGuid();

    @ErrorDef(httpStatusCode = 404, code = UserErrorCode.USER_NOT_FOUND,
            description = "User not found {0}")
    AppError userNotFound(String userId);

    @ErrorDef(httpStatusCode = 400, code = UserErrorCode.USER_STATUS_INVALID,
            description = "User status invalid")
    AppError userStatusInvalid();

    @ErrorDef(httpStatusCode = 400, code = PaymentErrorCode.PAYMENT_INSTRUMENT_STATUS_INVALID,
            description = "Payment instrument {0} status invalid.")
    AppError paymentInstrumentStatusInvalid(String paymentInstrumentId);

    @ErrorDef(httpStatusCode = 404, code = PaymentErrorCode.PAYMENT_INSTRUMENT_NOT_FOUND,
            description = "Payment instrument {0} not found.")
    AppError paymentInstrumentNotFound(String paymentInstrumentId);

    @ErrorDef(httpStatusCode = 500, code = PaymentErrorCode.PAYMENT_CONNECTION_ERROR,
            description = "Payment service connection error")
    AppError paymentConnectionError();

    @ErrorDef(httpStatusCode = 403, code = ErrorCode.INVALID_FIELD,
            description = "{1}", field = "{0}")
    AppError fieldInvalid(String field, String message);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.INVALID_FIELD,
            description = "Field value invalid", field = "{0}")
    AppError fieldInvalid(String field);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.ENUM_CONVERSION_ERROR,
            description = "Enum value {0} not exists in type {1}")
    AppError enumConversionError(String enumValue, String enumType);

    @ErrorDef(httpStatusCode = 400, code = PaymentErrorCode.PAYMENT_TYPE_NOT_SUPPORTED,
            description = "Payment instrument type {0} not supported")
    AppError piTypeNotSupported(String type);

    @ErrorDef(httpStatusCode = 404, code = CatalogErrorCode.OFFER_NOT_FOUND,
            description = "Offer {0} not found")
    AppError offerNotFound(String offerId);

    @ErrorDef(httpStatusCode = 500, code = CatalogErrorCode.CATALOG_CONNECTION_ERROR,
            description = "Catalog service connection error")
    AppError catalogConnectionError();

    @ErrorDef(httpStatusCode = 500, code = ErrorCode.UNEXPECTED_ERROR,
            description = "Unexpected Error")
    AppError unexpectedError();

    @ErrorDef(httpStatusCode = 400, code = RatingErrorCode.RATING_RESULT_INVALID,
            description = "Rating result invalid")
    AppError ratingResultInvalid();

    @ErrorDef(httpStatusCode = 500, code = RatingErrorCode.RATING_CONNECTION_ERROR,
            description = "Rating connection error")
    AppError ratingConnectionError();

    @ErrorDef(httpStatusCode = 500, code = BillingErrorCode.BILLING_CONNECTION_ERROR,
            description = "Billing connection error")
    AppError billingConnectionError(AppError[] causes);

    @ErrorDef(httpStatusCode = 500, code = BillingErrorCode.BILLING_CONNECTION_ERROR,
            description = "Billing connection error")
    AppError billingConnectionError();

    @ErrorDef(httpStatusCode = 500, code = BillingErrorCode.BALANCE_NOT_FOUND,
            description = "Balance not found")
    AppError balanceNotFound();

    @ErrorDef(httpStatusCode = 409, code = BillingErrorCode.BILLING_CHARGE_FAILED,
            description = "Billing charge failed")
    AppError billingChargeFailed();

    @ErrorDef(httpStatusCode = 404, code = ErrorCode.ORDER_EVENT_NOT_FOUND,
            description = "Order event not found")
    AppError orderEventNotFound();

    @ErrorDef(httpStatusCode = 500, code = UserErrorCode.USER_CONNECTION_ERROR,
            description = "User connection error")
    AppError userConnectionError();

    @ErrorDef(httpStatusCode = 500, code = FulfillmentErrorCode.FULFILLMENT_CONNECTION_ERROR,
            description = "Fulfillment connection error")
    AppError fulfillmentConnectionError();

    @ErrorDef(httpStatusCode = 500, code = FulfillmentErrorCode.FULFILLMENT_CONNECTION_ERROR,
            description = "Fulfilment connection error")
    AppError fulfilmentConnectionError(AppError[] causes);

    @ErrorDef(httpStatusCode = 404, code = ErrorCode.SUBLEDGER_NOT_FOUND,
            description = "SubledgerNotFound")
    AppError subledgerNotFound();
}
