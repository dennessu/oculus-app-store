/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.spec.error;

import com.junbo.common.error.AppError;
import com.junbo.common.error.ErrorDef;
import com.junbo.common.error.ErrorProxy;
import com.junbo.entitlement.common.def.Function;
import com.junbo.entitlement.common.exception.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Interface for AppError.
 * Copied from identity.
 */

public interface AppErrors {
    Map<Class, Function<AppError, EntitlementException>> ENTITLEMENT_EXCEPTION_REST_MAP
            = new HashMap<Class, Function<AppError, EntitlementException>>() {{
        put(NotFoundException.class, new Function<AppError, EntitlementException>() {
            @Override
            public AppError apply(EntitlementException e) {
                NotFoundException ex = (NotFoundException) e;
                return INSTANCE.notFound(ex.getEntity(), ex.getId());
            }
        });
        put(ExpirationTimeBeforeGrantTimeException.class, new Function<AppError, EntitlementException>() {
            @Override
            public AppError apply(EntitlementException e) {
                return INSTANCE.expirationTimeBeforeGrantTime();
            }
        });
        put(FieldNotMatchException.class, new Function<AppError, EntitlementException>() {
            @Override
            public AppError apply(EntitlementException e) {
                FieldNotMatchException ex = (FieldNotMatchException) e;
                return INSTANCE.fieldNotMatch(ex.getFieldName(), ex.getActual(), ex.getExpected());
            }
        });
        put(MissingFieldException.class, new Function<AppError, EntitlementException>() {
            @Override
            public AppError apply(EntitlementException e) {
                MissingFieldException ex = (MissingFieldException) e;
                return INSTANCE.missingParameterField(ex.getFieldName());
            }
        });
        put(NotTransferableException.class, new Function<AppError, EntitlementException>() {
            @Override
            public AppError apply(EntitlementException e) {
                NotTransferableException ex = (NotTransferableException) e;
                return INSTANCE.notTransferable(ex.getId(), ex.getReason());
            }
        });
        put(FieldNotCorrectException.class, new Function<AppError, EntitlementException>() {
            @Override
            public AppError apply(EntitlementException e) {
                FieldNotCorrectException ex = (FieldNotCorrectException) e;
                return INSTANCE.fieldNotCorrect(ex.getFieldName(), ex.getMsg());
            }
        });
        put(EntitlementException.class,
                new Function<AppError, EntitlementException>() {
                    @Override
                    public AppError apply(EntitlementException e) {
                        return INSTANCE.common(e.getMessage());
                    }
                });
    }};

    com.junbo.entitlement.spec.error.AppErrors INSTANCE =
            ErrorProxy.newProxyInstance(com.junbo.entitlement.spec.error.AppErrors.class);

    @ErrorDef(httpStatusCode = 400, code = "10000", description = "Missing Input field. field: {0}")
    AppError missingParameterField(String field);

    @ErrorDef(httpStatusCode = 400, code = "10001", description = "Unnecessary field found. field: {0}")
    AppError unnecessaryParameterField(String field);

    @ErrorDef(httpStatusCode = 403, code = "10002",
            description = "{0} doesn't match. actually: {1}, expected: {2}.")
    AppError fieldNotMatch(String fieldName, String actually, String expected);

    @ErrorDef(httpStatusCode = 404, code = "10003", description = "{0} [{1}] not found.")
    AppError notFound(String entity, String id);

    @ErrorDef(httpStatusCode = 400, code = "10004", description = "ExpirationTime should not be before grantTime.")
    AppError expirationTimeBeforeGrantTime();

    @ErrorDef(httpStatusCode = 400, code = "10005", description = "Field {0} is not correct. {1}")
    AppError fieldNotCorrect(String fieldName, String msg);

    @ErrorDef(httpStatusCode = 400, code = "10006", description = "Entitlement [{0}] is not transferable. {1}")
    AppError notTransferable(String id, String msg);

    @ErrorDef(httpStatusCode = 400, code = "10007", description = "Validation failed. {0}")
    AppError validation(String msg);

    @ErrorDef(httpStatusCode = 400, code = "10008", description = "{0}")
    AppError common(String msg);

    @ErrorDef(httpStatusCode = 500, code = "10009", description = "UnCaught Exception. {0}")
    AppError unCaught(String msg);
}
