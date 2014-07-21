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
    @ErrorDef(httpStatusCode = 404, code = "101", message = "Order Not Found.")
    AppError orderNotFound();

    @ErrorDef(httpStatusCode = 412, code = "102", message = "Order Action Not Supported.", field = "action", reason = "Order action {0} is not supported.")
    AppError orderActionNotSupported(String action);

    @ErrorDef(httpStatusCode = 412, code = "103", message = "Order Item Not Found.")
    AppError orderItemNotFound();

    @ErrorDef(httpStatusCode = 409, code = "104", message = "Order Concurrent Update.")
    AppError orderConcurrentUpdate();

    @ErrorDef(httpStatusCode = 412, code = "105", message = "User Not Found.", field = "userId", reason = "User {0} not found.")
    AppError userNotFound(String userId);

    @ErrorDef(httpStatusCode = 412, code = "106", message = "User Status Invalid.")
    AppError userStatusInvalid();

    @ErrorDef(httpStatusCode = 412, code = "107", message = "Payment Instrument Status Invalid.", field = "paymentInstrumentId",
            reason = "Payment instrument {0} status invalid.")
    AppError paymentInstrumentStatusInvalid(String paymentInstrumentId);

    @ErrorDef(httpStatusCode = 412, code = "108", message = "Payment instrument {0} not found.")
    AppError paymentInstrumentNotFound(String paymentInstrumentId);

    @ErrorDef(httpStatusCode = 500, code = "109", message = "Payment Connection Error.")
    AppError paymentConnectionError(AppError[] causes);

    @ErrorDef(httpStatusCode = 500, code = "109", message = "Payment Connection Error.", reason = "Payment service unavailable: {0}.")
    AppError paymentConnectionError(String error);

    @ErrorDef(httpStatusCode = 400, code = "110", message = "Enum Conversion Error.", reason = "Enum value {0} not exists in type {1}.")
    AppError enumConversionError(String enumValue, String enumType);

    @ErrorDef(httpStatusCode = 412, code = "111", message = "PI Type Not Supported.", reason = "Payment instrument type {0} not supported.")
    AppError piTypeNotSupported(String type);

    @ErrorDef(httpStatusCode = 412, code = "112", message = "Offer Not Found.", field = "offerId", reason = "Offer {0} not found.")
    AppError offerNotFound(String offerId);

    @ErrorDef(httpStatusCode = 500, code = "113", message = "Catalog Connection Error.")
    AppError catalogConnectionError();

    @ErrorDef(httpStatusCode = 500, code = "114", message = "Rating Result Invalid.", reason = "Rating result invalid: {0}.")
    AppError ratingResultInvalid(String cause);

    @ErrorDef(httpStatusCode = 500, code = "115", message = "Rating Connection Error.")
    AppError ratingConnectionError(AppError error);

    @ErrorDef(httpStatusCode = 500, code = "115", message = "Rating Connection Error.", reason = "Order rating error: {0}.")
    AppError ratingConnectionError(String error);

    @ErrorDef(httpStatusCode = 500, code = "116", message = "Billing Connection Error.")
    AppError billingConnectionError(AppError[] causes);

    @ErrorDef(httpStatusCode = 500, code = "116", message = "Billing Connection Error.", reason = "Billing service returns error: {0}.")
    AppError billingConnectionError(String causes);

    @ErrorDef(httpStatusCode = 500, code = "117", message = "Billing Result Invalid.", reason = "Billing result invalid: {0}.")
    AppError billingResultInvalid(String cause);

    @ErrorDef(httpStatusCode = 412, code = "118", message = "Balance Not Found.")
    AppError balanceNotFound();

    @ErrorDef(httpStatusCode = 412, code = "119", message = "Billing Charge Failed.")
    AppError billingChargeFailed();

    @ErrorDef(httpStatusCode = 412, code = "120", message = "Billing Insufficient Fund.")
    AppError billingInsufficientFund();

    @ErrorDef(httpStatusCode = 500, code = "121", message = "User Connection Error.")
    AppError userConnectionError();

    @ErrorDef(httpStatusCode = 500, code = "122", message = "Fulfillment Connection Error.", reason = "Fulfilment service error: {0}.")
    AppError fulfillmentConnectionError(String cause);

    @ErrorDef(httpStatusCode = 500, code = "122", message = "Fulfilment service error.")
    AppError fulfilmentConnectionError(AppError[] causes);

    @ErrorDef(httpStatusCode = 412, code = "123", message = "Subledger Not Found.")
    AppError subledgerNotFound();

    @ErrorDef(httpStatusCode = 409, code = "124", message = "Subledger Concurrent Update.")
    AppError subledgerConcurrentUpdate();

    @ErrorDef(httpStatusCode = 412, code = "125", message = "Invalid Settled Order Update.")
    AppError invalidSettledOrderUpdate();

    @ErrorDef(httpStatusCode = 412, code = "126", message = "Event Not Supported.", reason = "Event(action:{0}, status:{1}) is not supported.")
    AppError eventNotSupported(String action, String status);

    @ErrorDef(httpStatusCode = 412, code = "126", message = "Event Not Supported.", reason = "Event(action:{0}, status:{1}) is not expected.")
    AppError eventNotExpected(String action, String status);

    @ErrorDef(httpStatusCode = 412, code = "127", message = "Balance Confirm Failed.")
    AppError balanceConfirmFailed();

    @ErrorDef(httpStatusCode = 412, code = "128", message = "Order Price Changed.")
    AppError orderPriceChanged();

    @ErrorDef(httpStatusCode = 412, code = "129", message = "Order Is Refunded.")
    AppError orderIsRefunded();

    @ErrorDef(httpStatusCode = 412, code = "130", message = "Order item Is Not Found For Refund", reason = "Offer {0} is not found for refund.")
    AppError orderItemIsNotFoundForRefund(String offer);

    @ErrorDef(httpStatusCode = 412, code = "131", message = "Order Not Refundable.")
    AppError orderNotRefundable();

    @ErrorDef(httpStatusCode = 412, code = "132", message = "Billing Refund Failed.", reason = "Billing refund failed: {0}.")
    AppError billingRefundFailed(String reason);

    @ErrorDef(httpStatusCode = 412, code = "133", message = "Billing Audit Failed.", reason = "Billing refund failed: {0}.")
    AppError billingAuditFailed(String reason);

    @ErrorDef(httpStatusCode = 412, code = "134", message = "Currency Not Valid.", reason = "Currency {0} is not valid.")
    AppError currencyNotValid(String currency);

    @ErrorDef(httpStatusCode = 412, code = "135", message = "Order No Item Refund.")
    AppError orderNoItemRefund();

    @ErrorDef(httpStatusCode = 412, code = "136", message = "Calculate Tax Error.")
    AppError calculateTaxError(AppError error);

    @ErrorDef(httpStatusCode = 412, code = "136", message = "Calculate Tax Error.", reason = "Failed to calculate tax: {0}.")
    AppError calculateTaxError(String error);

    @ErrorDef(httpStatusCode = 412, code = "137", message = "Order Risk Review Error.", reason = "Failed to review risk of order: {0}.")
    AppError orderRiskReviewError(String error);

    @ErrorDef(httpStatusCode = 412, code = "138", message = "Order Event Status Not Match.")
    AppError orderEvenStatusNotMatch();
}
