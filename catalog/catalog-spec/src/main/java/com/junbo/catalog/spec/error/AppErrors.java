/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.error;

import com.junbo.common.error.AppError;
import com.junbo.common.error.ErrorDef;
import com.junbo.common.error.ErrorProxy;

/**
 * Interface for AppError.
 * Copied from identity.
 */

public interface AppErrors {
    AppErrors INSTANCE = ErrorProxy.newProxyInstance(AppErrors.class);

    @ErrorDef(httpStatusCode = 412, code = "101", message = "Offer Not Found",
            field = "{0}", reason = "Offer with ID {1} is not found")
    AppError offerNotFound(String field, String offerId);

    @ErrorDef(httpStatusCode = 412, code = "102", message = "Item Not Found",
            field = "{0}", reason = "Item with ID {1} is not found")
    AppError itemNotFound(String field, String itemId);

    @ErrorDef(httpStatusCode = 412, code = "103", message = "Genre Not Found",
            field = "{0}", reason = "Genre with ID {1} is not found")
    AppError genreNotFound(String field, String genreId);

    @ErrorDef(httpStatusCode = 412, code = "104", message = "Category Not Found",
            field = "{0}", reason = "Category with ID {1} is not found")
    AppError categoryNotFound(String field, String categoryId);

    @ErrorDef(httpStatusCode = 409, code = "105", message = "Duplicate Package Name",
            field = "packageName", reason = "Package name {0} already exists")
    AppError duplicatePackageName(String packageName);
}
