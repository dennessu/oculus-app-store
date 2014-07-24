/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.fulfilment.spec.error;

import com.junbo.common.error.AppError;
import com.junbo.common.error.ErrorDef;
import com.junbo.common.error.ErrorProxy;
import com.junbo.common.id.OrderId;
import com.junbo.common.id.UserPersonalInfoId;

/**
 * Fulfilment AppError.
 */

public interface AppErrors {
    AppErrors INSTANCE = ErrorProxy.newProxyInstance(com.junbo.fulfilment.spec.error.AppErrors.class);

    @ErrorDef(httpStatusCode = 412, code = "101", message = "Fulfillment Request Not Found",
            field = "orderId", reason = "Fulfillment Request not found by order ID {0}")
    AppError fulfillmentRequestNotFound(OrderId orderId);

    @ErrorDef(httpStatusCode = 409, code = "102", message = "Fulfillment Request Already Exists",
            field = "orderId", reason = "Fulfillment Request for order ID {0} already exists")
    AppError fulfillmentRequestAlreadyExists(OrderId orderId);

    @ErrorDef(httpStatusCode = 412, code = "103", message = "Shipping Address Not Found",
            field = "shippingAddress", reason = "Shipping address with ID {0} is not found.")
    AppError shippingAddressNotFound(UserPersonalInfoId addressId);

    @ErrorDef(httpStatusCode = 412, code = "104", message = "Item Not Found",
            field = "item", reason = "Item with ID {0} is not found.")
    AppError itemNotFound(Object itemId);

    @ErrorDef(httpStatusCode = 412, code = "105", message = "Offer Not Found",
            field = "offer", reason = "Offer with ID {0} is not found.")
    AppError offerNotFound(Object offerId);

    @ErrorDef(httpStatusCode = 500, code = "106", message = "Gateway Failure", reason = "{0} Gateway Failure")
    AppError gatewayFailure(String gateway);
}
