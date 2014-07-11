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
            message ="Invalid null/empty input parameter")
    AppError invalidNullEmptyInputParam();

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.INVALID_OBJECT_TYPE,
            message = "Object type doesn't map. actually: {0}, expected: {1}.")
    AppError invalidObjectType(Class actually, Class expected);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.MISSING_PARAMETER_FIELD,
            message = "Missing Input field. field: {0}")
    AppError missingParameterField(String field);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.UNNECESSARY_PARAMETER_FIELD,
            message = "Unnecessary field found. field: {0}")
    AppError unnecessaryParameterField(String field);

    @ErrorDef(httpStatusCode = 404, code = ErrorCode.ORDER_NOT_FOUND,
            message = "Order not found")
    AppError orderNotFound();

    @ErrorDef(httpStatusCode = 500, code = ErrorCode.ORDER_TYPE_NOT_SUPPORTED,
            message = "Order action {0} is not supported")
    AppError orderTypeNotSupported(String type);

    @ErrorDef(httpStatusCode = 500, code = ErrorCode.ORDER_ACTION_NOT_SUPPORTED,
            message = "Order action {0} is not supported")
    AppError orderActionNotSupported(String action);

    @ErrorDef(httpStatusCode = 404, code = ErrorCode.ORDER_ITEM_NOT_FOUND,
            message = "Order item not found")
    AppError orderItemNotFound();

    @ErrorDef(httpStatusCode = 409, code = ErrorCode.ORDER_NOT_TENTATIVE,
            message = "Order not tentative")
    AppError orderNotTentative();

    @ErrorDef(httpStatusCode = 409, code = ErrorCode.ORDER_CONCURRENT_UPDATE,
            message = "Concurrent update of order detected")
    AppError orderConcurrentUpdate();

    @ErrorDef(httpStatusCode = 404, code = UserErrorCode.USER_NOT_FOUND,
            message = "User not found {0}")
    AppError userNotFound(String userId);

    @ErrorDef(httpStatusCode = 400, code = UserErrorCode.USER_STATUS_INVALID,
            message = "User status invalid")
    AppError userStatusInvalid();

    @ErrorDef(httpStatusCode = 400, code = PaymentErrorCode.PAYMENT_INSTRUMENT_STATUS_INVALID,
            message = "Payment instrument {0} status invalid.")
    AppError paymentInstrumentStatusInvalid(String paymentInstrumentId);

    @ErrorDef(httpStatusCode = 404, code = PaymentErrorCode.PAYMENT_INSTRUMENT_NOT_FOUND,
            message = "Payment instrument {0} not found.")
    AppError paymentInstrumentNotFound(String paymentInstrumentId);

    @ErrorDef(httpStatusCode = 500, code = PaymentErrorCode.PAYMENT_CONNECTION_ERROR,
            message = "Payment service connection error")
    AppError paymentConnectionError();

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.INVALID_FIELD,
            message = "{1}", field = "{0}")
    AppError fieldInvalid(String field, String message);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.INVALID_FIELD,
            message = "Field value invalid", field = "{0}")
    AppError fieldInvalid(String field);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.ENUM_CONVERSION_ERROR,
            message = "Enum value {0} not exists in type {1}")
    AppError enumConversionError(String enumValue, String enumType);

    @ErrorDef(httpStatusCode = 400, code = PaymentErrorCode.PAYMENT_TYPE_NOT_SUPPORTED,
            message = "Payment instrument type {0} not supported")
    AppError piTypeNotSupported(String type);

    @ErrorDef(httpStatusCode = 404, code = CatalogErrorCode.OFFER_NOT_FOUND,
            message = "Offer {0} not found")
    AppError offerNotFound(String offerId);

    @ErrorDef(httpStatusCode = 500, code = CatalogErrorCode.CATALOG_CONNECTION_ERROR,
            message = "Catalog service connection error")
    AppError catalogConnectionError();

    @ErrorDef(httpStatusCode = 500, code = ErrorCode.UNEXPECTED_ERROR,
            message = "Order service failure: {0}")
    AppError unexpectedError(String cause);

    @ErrorDef(httpStatusCode = 400, code = RatingErrorCode.RATING_RESULT_INVALID,
            message = "Rating result invalid: {0}")
    AppError ratingResultInvalid(String cause);

    @ErrorDef(httpStatusCode = 500, code = RatingErrorCode.RATING_CONNECTION_ERROR,
            message = "Order rating error")
    AppError ratingConnectionError(AppError error);

    @ErrorDef(httpStatusCode = 500, code = RatingErrorCode.RATING_CONNECTION_ERROR,
            message = "Order rating error: {0}")
    AppError ratingConnectionError(String error);

    @ErrorDef(httpStatusCode = 500, code = BillingErrorCode.BILLING_CONNECTION_ERROR,
            message = "Billing service returns error")
    AppError billingConnectionError(AppError[] causes);

    @ErrorDef(httpStatusCode = 500, code = BillingErrorCode.BILLING_CONNECTION_ERROR,
            message = "Billing service returns error: {0}")
    AppError billingConnectionError(String causes);

    @ErrorDef(httpStatusCode = 500, code = BillingErrorCode.BILLING_RESULT_INVALID,
            message = "Billing result invalid: {0}")
    AppError billingResultInvalid(String cause);

    @ErrorDef(httpStatusCode = 500, code = BillingErrorCode.BALANCE_NOT_FOUND,
            message = "Balance not found")
    AppError balanceNotFound();

    @ErrorDef(httpStatusCode = 409, code = BillingErrorCode.BILLING_CHARGE_FAILED,
            message = "Billing charge failed")
    AppError billingChargeFailed();

    @ErrorDef(httpStatusCode = 409, code = BillingErrorCode.BILLING_INSUFFICIENT_FUND,
            message = "Insufficient fund")
    AppError billingInsufficientFund();

    @ErrorDef(httpStatusCode = 404, code = ErrorCode.ORDER_EVENT_NOT_FOUND,
            message = "Order event not found")
    AppError orderEventNotFound();

    @ErrorDef(httpStatusCode = 500, code = UserErrorCode.USER_CONNECTION_ERROR,
            message = "User connection error")
    AppError userConnectionError();

    @ErrorDef(httpStatusCode = 500, code = FulfillmentErrorCode.FULFILLMENT_CONNECTION_ERROR,
            message = "Fulfilment service error: {0}")
    AppError fulfillmentConnectionError(String cause);

    @ErrorDef(httpStatusCode = 500, code = FulfillmentErrorCode.FULFILLMENT_CONNECTION_ERROR,
            message = "Fulfilment service error")
    AppError fulfilmentConnectionError(AppError[] causes);

    @ErrorDef(httpStatusCode = 404, code = ErrorCode.SUBLEDGER_NOT_FOUND,
            message = "SubledgerNotFound")
    AppError subledgerNotFound();

    @ErrorDef(httpStatusCode = 404, code = ErrorCode.SUBLEDGER_CONCURRENT_UPDATE,
            message = "Concurrent update of subledger detected")
    AppError subledgerConcurrentUpdate();

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.INVALID_SETTLED_ORDER_UPDATE,
            message = "Invalid settled order update")
    AppError invalidSettledOrderUpdate();

    @ErrorDef(httpStatusCode = 412, code = ErrorCode.EVENT_NOT_SUPPORTED,
            message = "Event(action:{0}, status:{1}) is not supported")
    AppError eventNotSupported(String action, String status);

    @ErrorDef(httpStatusCode = 400, code = BillingErrorCode.BILLING_CONFIRM_BALANCE_FAILED,
            message = "Fail to confirm balance")
    AppError balanceConfirmFailed();

    @ErrorDef(httpStatusCode = 409, code = ErrorCode.ORDER_CAN_NOT_BE_CANCELED,
            message = "Order can not be canceled")
    AppError orderNotCancelable();

    @ErrorDef(httpStatusCode = 409, code = ErrorCode.ORDER_PRICE_CHANGED,
            message = "Order price is changed")
    AppError orderPriceChanged();

    @ErrorDef(httpStatusCode = 409, code = ErrorCode.ORDER_IS_REFUNDED,
            message = "Order is refunded")
    AppError orderIsRefunded();

    @ErrorDef(httpStatusCode = 409, code = ErrorCode.ORDER_ITEM_IS_NOT_FOUND_FOR_REFUNDED,
            message = "Offer {0} is not found for refund")
    AppError orderItemIsNotFoundForRefund(String offer);

    @ErrorDef(httpStatusCode = 409, code = ErrorCode.ORDER_CAN_NOT_BE_REFUNDED,
            message = "Order can not be refunded or cancelled")
    AppError orderNotRefundable();

    @ErrorDef(httpStatusCode = 500, code = BillingErrorCode.BILLING_REFUND_FAILED,
            message = "Billing refund failed: {0}")
    AppError billingRefundFailed(String reason);

    @ErrorDef(httpStatusCode = 500, code = BillingErrorCode.BILLING_AUDIT_FAILED,
            message = "Billing refund failed: {0}")
    AppError billingAuditFailed(String reason);

    @ErrorDef(httpStatusCode = 400, code = UserErrorCode.CURRENCY_NOT_VALID,
            message = "Currency {0} is not valid")
    AppError currencyNotValid(String currency);

    @ErrorDef(httpStatusCode = 400, code = ErrorCode.ORDER_NO_ITEM_TO_REFUND_IN_REQUEST,
            message = "There's no item to refund per request")
    AppError orderNoItemRefund();

    @ErrorDef(httpStatusCode = 409, code = BillingErrorCode.BILLING_TAX_FAILED,
            message = "Failed to calculate tax")
    AppError calculateTaxError(AppError error);

    @ErrorDef(httpStatusCode = 409, code = BillingErrorCode.BILLING_TAX_FAILED,
            message = "Failed to calculate tax: {0}")
    AppError calculateTaxError(String error);

    @ErrorDef(httpStatusCode = 500, code = ErrorCode.ORDER_RISK_REVIEW_ERROR,
            message = "Failed to review risk of order: {0}")
    AppError orderRiskReviewError(String error);

    @ErrorDef(httpStatusCode = 409, code = ErrorCode.ORDER_EVENT_STATUS_NOT_MATCH,
            message = "Order event status not match")
    AppError orderEvenStatusNotMatch();
}
