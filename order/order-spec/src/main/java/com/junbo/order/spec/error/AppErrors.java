/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.error;

import com.junbo.common.error.AppError;
import com.junbo.common.error.ErrorDef;
import com.junbo.common.error.ErrorDetail;
import com.junbo.common.error.ErrorProxy;
import com.junbo.common.id.PayoutId;

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

    @ErrorDef(httpStatusCode = 412, code = "108", message = "Payment Instrument Not Found.", reason = "Payment instrument {0} not found.")
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

    @ErrorDef(httpStatusCode = 500, code = "116", message = "Billing Throws Unexpected Error.")
    AppError billingConnectionError(AppError[] causes);

    @ErrorDef(httpStatusCode = 500, code = "116", message = "Billing Throws Unexpected Error.", reason = "Billing service returns error: {0}.")
    AppError billingConnectionError(String causes);

    @ErrorDef(httpStatusCode = 500, code = "117", message = "Billing Result Invalid.", reason = "Billing result invalid: {0}.")
    AppError billingResultInvalid(String cause);

    @ErrorDef(httpStatusCode = 412, code = "118", message = "Balance Not Found.")
    AppError balanceNotFound();

    @ErrorDef(httpStatusCode = 412, code = "119", message = "The payment of order is declined.")
    AppError billingChargeFailed();

    @ErrorDef(httpStatusCode = 412, code = "120", message = "Insufficient Fund.")
    AppError billingInsufficientFund();

    @ErrorDef(httpStatusCode = 500, code = "121", message = "User Connection Error.")
    AppError userConnectionError();

    @ErrorDef(httpStatusCode = 500, code = "122", message = "Fulfillment Connection Error.", reason = "Fulfilment service error: {0}.")
    AppError fulfillmentConnectionError(String cause);

    @ErrorDef(httpStatusCode = 500, code = "122", message = "Fulfilment Connection error.")
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

    @ErrorDef(httpStatusCode = 412, code = "127", message = "Billing Web Payment Charge Failed.")
    AppError billingWebpaymentChargeFailed();

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

    @ErrorDef(httpStatusCode = 412, code = "139", message = "Country Not Valid.", reason = "Country {0} is not valid.")
    AppError countryNotValid(String country);

    @ErrorDef(httpStatusCode = 412, code = "140", message = "Offer item type not valid.")
    AppError offerItemTypeNotValid();
    
    @ErrorDef(httpStatusCode = 412, code = "141", message = "Order Not capturable.")
    AppError orderNotCapturable();

    @ErrorDef(httpStatusCode = 500, code = "142", message = "Identity Connection Error.")
    AppError identityConnectionError();

    @ErrorDef(httpStatusCode = 500, code = "143", message = "Identity Result Invalid.", reason = "Identity result invalid: {0}.")
    AppError identityResultInvalid(String cause);

    @ErrorDef(httpStatusCode = 500, code = "144", message = "Catalog Result Invalid.", reason = "Catalog result invalid: {0}.")
    AppError catalogResultInvalid(String cause);

    @ErrorDef(httpStatusCode = 500, code = "145", message = "Entitlement Connection Error.")
    AppError entitlementConnectionError();

    @ErrorDef(httpStatusCode = 412, code = "146", message = "Duplicate Purchase.")
    AppError duplicatePurchase();

    @ErrorDef(httpStatusCode = 412, code = "147", message = "Rating result error.")
    AppError ratingResultError(ErrorDetail[] errorDetails);

    @ErrorDef(httpStatusCode = 412, code = "148", message = "Free Offer Required")
    AppError notFreeOrder();

    @ErrorDef(httpStatusCode = 412, code = "149", message = "Too Many Order Items", reason = "The number of items can not exceed: {0}")
    AppError tooManyItems(int count);

    @ErrorDef(httpStatusCode = 412, code = "150", message = "Too Many Offers", reason = "The number of offers can not exceed: {0}")
    AppError tooManyOffers(int count);

    @ErrorDef(httpStatusCode = 412, code = "151", message = "Offer Not Purchasable", reason = "Order contains offers that not purchasable.")
    AppError offerNotPurchasable();

    @ErrorDef(httpStatusCode = 412, code = "152", message = "Order is already in settlement process. Get order to see the latest order status")
    AppError orderAlreadyInSettleProcess();

    @ErrorDef(httpStatusCode = 412, code = "300", message = "Subledgers not found by payout Id.", reason = "Subledgers not found by payout Id:{0}")
    AppError subledgerNotFoundByPayoutId(PayoutId payoutId);
}
