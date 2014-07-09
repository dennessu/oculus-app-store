/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.error;

import com.google.common.base.Strings;
import com.junbo.common.id.Id;
import com.junbo.common.util.IdFormatter;
import com.junbo.langur.core.context.JunboHttpContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * Interface for AppError.
 */
public class ErrorProxy implements InvocationHandler {

    @SuppressWarnings("unchecked")
    public static <T> T newProxyInstance(Class<T> klass) {
        return (T) Proxy.newProxyInstance(klass.getClassLoader(), new Class<?>[]{klass}, new ErrorProxy());
    }

    @Override
    public Object invoke(Object proxy, Method method, final Object[] args) throws java.lang.Throwable {
        ErrorDef errorDef = method.getAnnotation(ErrorDef.class);
        if (errorDef == null) {
            return method.invoke(proxy, args);
        }

        final int httpStatusCode = errorDef.httpStatusCode();

        final String componentCode = Components.instance().getComponent(JunboHttpContext.getRequestHandler());

        final String code;
        if (errorDef.code().isEmpty()) {
            throw new RuntimeException("Invalid ErrorDef: code is required.");
        }
        else {
            code = componentCode + "." + errorDef.code();
        }

        final String message;
        if (errorDef.message().isEmpty()) {
            message = null;
        }
        else if (args == null || args.length == 0){
            message = errorDef.message();
        }
        else {
            message = formatMessage(errorDef.message(), args);
        }

        final String field;
        if (errorDef.field().isEmpty()) {
            field = null;
        }
        else if (args == null || args.length == 0) {
            field = errorDef.field();
        }
        else {
            field = formatMessage(errorDef.field(), args);
        }

        final String reason;
        if (errorDef.reason().isEmpty()) {
            reason = null;
        }
        else if (args == null || args.length == 0) {
            reason = errorDef.reason();
        }
        else {
            reason = formatMessage(errorDef.reason(), args);
        }

        List<ErrorDetail> errorDetails = new ArrayList<>();
        if (!Strings.isNullOrEmpty(field) || !Strings.isNullOrEmpty(reason)) {
            ErrorDetail detail = new ErrorDetail(field, reason);
            errorDetails.add(detail);
        }

        if (args != null && args.length > 0 && (args[args.length - 1] instanceof ErrorDetail[])) {
            ErrorDetail[] detailsArray = (ErrorDetail[]) args[args.length - 1];
            if (detailsArray != null && detailsArray.length != 0) {
                errorDetails.addAll(Arrays.asList(detailsArray));
            }
        }
        if (errorDetails.size() == 0) {
            errorDetails = null;
        }

        final List<ErrorDetail> finalErrorDetails = errorDetails;

        return new AppError() {
            private Error error = new Error(
                    message,
                    code,
                    finalErrorDetails);

            @Override
            public int getHttpStatusCode() {
                return httpStatusCode;
            }

            @Override
            public AppErrorException exception() {
                return new AppErrorException(this);
            }

            @Override
            public Error error() {
                return error;
            }

            @Override
            public String toString() {
                return error.toString();
            }
        };
    }

    private String formatMessage(String pattern, Object[] args) {
        if (pattern == null) {
            return null;
        }

        int index = 0;
        for (Object arg : args) {

            String argStr = "{null}";

            if (arg != null) {
                if (arg instanceof Id) {
                    argStr = IdFormatter.encodeId((Id) arg);
                } else {
                    argStr = arg.toString();
                }
            }

            pattern = pattern.replace("{"+index+"}", argStr);
            index++;
        }

        return pattern;
    }


}
