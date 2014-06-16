/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.cart.spec.error;

import com.junbo.common.error.AppError;
import com.junbo.common.error.ErrorDef;
import com.junbo.common.error.ErrorProxy;

/**
 * Interface for AppError.
 * HttpStatusCode please refer to http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
 */

public interface AppErrors {
    AppErrors INSTANCE = ErrorProxy.newProxyInstance(AppErrors.class);

    @ErrorDef(httpStatusCode = 404, code = ErrorCode.CART_NOT_FOUND, description ="Cart not found")
    AppError cartNotFound();

    @ErrorDef(httpStatusCode = 404, code = ErrorCode.CART_ITEM_NOT_FOUND, description ="Cart item not found")
    AppError cartItemNotFound();

    @ErrorDef(httpStatusCode = 403, code = ErrorCode.CART_ALREADY_EXIST,
            description ="Cart with name already exist. user:{0}, cartName:{1}")
    AppError cartAlreadyExists(Long userId, String cartName);

    @ErrorDef(httpStatusCode = 404, code = ErrorCode.USER_NOT_FOUND, description ="User not found")
    AppError userNotFound();

    @ErrorDef(httpStatusCode = 403, code = ErrorCode.USER_STATUS_INVALID, description ="User status invalid")
    AppError userStatusInvalid();

    @ErrorDef(httpStatusCode = 403, code = ErrorCode.INVALID_FIELD, description ="{1}", field = "{0}")
    AppError fieldInvalid(String field, String message);

    @ErrorDef(httpStatusCode = 403, code = ErrorCode.INVALID_FIELD, description ="Field value invalid", field = "{0}")
    AppError fieldInvalid(String field);

    @ErrorDef(httpStatusCode = 403, code = ErrorCode.INVALID_FIELD, description ="Field is client immutable", field = "{0}")
    AppError clientImmutable(String field);
}
