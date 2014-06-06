/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.error;

import com.junbo.common.id.Id;
import com.junbo.common.util.IdFormatter;

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

        final String methodName = method.getName();
        final int httpStatusCode = errorDef.httpStatusCode();

        final String code;
        if (errorDef.code().isEmpty()) {
            code = null;
        }
        else {
            code = errorDef.code();
        }

        final String message;
        if (errorDef.description().isEmpty()) {
            message = null;
        }
        else if (args == null || args.length == 0){
            message = errorDef.description();
        }
        else {
            message = formatMessage(errorDef.description(), args);
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

        final List<AppError> causes;
        if (args != null && args.length > 0 && (args[args.length - 1] instanceof AppError[])) {
            AppError[] causesArray = (AppError[]) args[args.length - 1];
            if (causesArray == null || causesArray.length == 0) {
                causes = null;
            }
            else {
                causes = Arrays.asList(causesArray);
            }
        }
        else {
            causes = null;
        }

        return new AppError() {

            @Override
            public int getHttpStatusCode() {
                return httpStatusCode;
            }

            @Override
            public String getCode() {
                return code;
            }

            @Override
            public String getDescription() {
                return message;
            }

            @Override
            public String getField() {
                return field;
            }

            @Override
            public List<AppError> getCauses() {
                return causes;
            }

            @Override
            public AppErrorException exception() {
                return new AppErrorException(this);
            }

            @Override
            public Error error() {
                List<Error> causeErrors = new ArrayList<Error>();
                if (causes != null) {
                    for (AppError cause : causes) {
                        causeErrors.add(cause.error());
                    }
                }
                return new Error(code, message, field, causeErrors);
            }

            @Override
            public String toString() {
                return methodName +
                        " { httpStatusCode=" + httpStatusCode +
                        ", code=" + code +
                        ", message=" + message +
                        ", field=" + field +
                        ", causes=" + causes + " }";
            }
        };
    }

    private String formatMessage(String pattern, Object[] args) {
        if (pattern == null) {
            return null;
        }

        int index = 0;
        for (Object arg : args) {

            String argStr = "null";

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
