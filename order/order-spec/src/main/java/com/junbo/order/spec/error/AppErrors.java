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

    @ErrorDef(httpStatusCode = 500, code = ErrorCode.ORDER_ACTION_NOT_SUPPORTED,
            description = "Order action {0} is not supported")
    AppError orderActionNotSupported(String action);

    @ErrorDef(httpStatusCode = 404, code = ErrorCode.ORDER_ITEM_NOT_FOUND,
            description = "Order item not found")
    AppError orderItemNotFound();

    @ErrorDef(httpStatusCode = 409, code = ErrorCode.ORDER_NOT_TENTATIVE,
            description = "Order not tentative")
    AppError orderNotTentative();

    @ErrorDef(httpStatusCode = 409, code = ErrorCode.ORDER_CONCURRENT_UPDATE,
            description = "Concurrent update of order detected")
    AppError orderConcurrentUpdate();

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

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.INVALID_FIELD,
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
            description = "Order service failure: {0}")
    AppError unexpectedError(String cause);

    @ErrorDef(httpStatusCode = 400, code = RatingErrorCode.RATING_RESULT_INVALID,
            description = "Rating result invalid: {0}")
    AppError ratingResultInvalid(String cause);

    @ErrorDef(httpStatusCode = 500, code = RatingErrorCode.RATING_CONNECTION_ERROR,
            description = "Order rating error")
    AppError ratingConnectionError(AppError error);

    @ErrorDef(httpStatusCode = 500, code = RatingErrorCode.RATING_CONNECTION_ERROR,
            description = "Order rating error: {0}")
    AppError ratingConnectionError(String error);

    @ErrorDef(httpStatusCode = 500, code = BillingErrorCode.BILLING_CONNECTION_ERROR,
            description = "Billing service returns error")
    AppError billingConnectionError(AppError[] causes);

    @ErrorDef(httpStatusCode = 500, code = BillingErrorCode.BILLING_CONNECTION_ERROR,
            description = "Billing service returns error: {0}")
    AppError billingConnectionError(String causes);

    @ErrorDef(httpStatusCode = 500, code = BillingErrorCode.BILLING_RESULT_INVALID,
            description = "Billing result invalid: {0}")
    AppError billingResultInvalid(String cause);

    @ErrorDef(httpStatusCode = 500, code = BillingErrorCode.BALANCE_NOT_FOUND,
            description = "Balance not found")
    AppError balanceNotFound();

    @ErrorDef(httpStatusCode = 409, code = BillingErrorCode.BILLING_CHARGE_FAILED,
            description = "Billing charge failed")
    AppError billingChargeFailed();

    @ErrorDef(httpStatusCode = 409, code = BillingErrorCode.BILLING_INSUFFICIENT_FUND,
            description = "Insufficient fund")
    AppError billingInsufficientFund();

    @ErrorDef(httpStatusCode = 404, code = ErrorCode.ORDER_EVENT_NOT_FOUND,
            description = "Order event not found")
    AppError orderEventNotFound();

    @ErrorDef(httpStatusCode = 500, code = UserErrorCode.USER_CONNECTION_ERROR,
            description = "User connection error")
    AppError userConnectionError();

    @ErrorDef(httpStatusCode = 500, code = FulfillmentErrorCode.FULFILLMENT_CONNECTION_ERROR,
            description = "Fulfilment service error: {0}")
    AppError fulfillmentConnectionError(String cause);

    @ErrorDef(httpStatusCode = 500, code = FulfillmentErrorCode.FULFILLMENT_CONNECTION_ERROR,
            description = "Fulfilment service error")
    AppError fulfilmentConnectionError(AppError[] causes);

    @ErrorDef(httpStatusCode = 404, code = ErrorCode.SUBLEDGER_NOT_FOUND,
            description = "SubledgerNotFound")
    AppError subledgerNotFound();

    @ErrorDef(httpStatusCode = 404, code = ErrorCode.SUBLEDGER_CONCURRENT_UPDATE,
            description = "Concurrent update of subledger detected")
    AppError subledgerConcurrentUpdate();

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.INVALID_SETTLED_ORDER_UPDATE,
            description = "Invalid settled order update")
    AppError invalidSettledOrderUpdate();

    @ErrorDef(httpStatusCode = 412, code = ErrorCode.EVENT_NOT_SUPPORTED,
            description = "Event(action:{0}, status:{1}) is not supported")
    AppError eventNotSupported(String action, String status);

    @ErrorDef(httpStatusCode = 400, code = BillingErrorCode.BILLING_CONFIRM_BALANCE_FAILED,
            description = "Fail to confirm balance")
    AppError balanceConfirmFailed();

    @ErrorDef(httpStatusCode = 409, code = ErrorCode.ORDER_CAN_NOT_BE_CANCELED,
            description = "Order can not be canceled")
    AppError orderNotCancelable();

    @ErrorDef(httpStatusCode = 409, code = ErrorCode.ORDER_PRICE_CHANGED,
            description = "Order price is changed")
    AppError orderPriceChanged();

    @ErrorDef(httpStatusCode = 409, code = ErrorCode.ORDER_IS_REFUNDED,
            description = "Order is refunded")
    AppError orderIsRefunded();

    @ErrorDef(httpStatusCode = 409, code = ErrorCode.ORDER_ITEM_IS_NOT_FOUND_FOR_REFUNDED,
            description = "Offer {0} is not found for refund")
    AppError orderItemIsNotFoundForRefund(String offer);

    @ErrorDef(httpStatusCode = 409, code = ErrorCode.ORDER_CAN_NOT_BE_REFUNDED,
            description = "Order can not be refunded")
    AppError orderNotRefundable();

    @ErrorDef(httpStatusCode = 500, code = BillingErrorCode.BILLING_REFUND_FAILED,
            description = "Billing refund failed: {0}")
    AppError billingRefundFailed(String reason);

    @ErrorDef(httpStatusCode = 400, code = UserErrorCode.CURRENCY_NOT_VALID,
            description = "Currency {0} is not valid")
    AppError currencyNotValid(String currency);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.ORDER_NO_ITEM_TO_REFUND_IN_REQUEST,
            description = "There's no item to refund per request")
    AppError orderNoItemRefund();

    @ErrorDef(httpStatusCode = 409, code = BillingErrorCode.BILLING_TAX_FAILED,
            description = "Failed to calculate tax")
    AppError calculateTaxError(AppError error);

    @ErrorDef(httpStatusCode = 409, code = BillingErrorCode.BILLING_TAX_FAILED,
            description = "Failed to calculate tax: {0}")
    AppError calculateTaxError(String error);

    @ErrorDef(httpStatusCode = 500, code = ErrorCode.ORDER_RISK_REVIEW_ERROR,
            description = "Failed to review risk of order: {0}")
    AppError orderRiskReviewError(String error);

    @ErrorDef(httpStatusCode = 409, code = ErrorCode.ORDER_EVENT_STATUS_NOT_MATCH,
            description = "Order event status not match")
    AppError orderEvenStatusNotMatch();
}
