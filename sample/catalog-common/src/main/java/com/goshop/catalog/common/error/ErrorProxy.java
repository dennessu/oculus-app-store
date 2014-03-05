package com.goshop.catalog.common.error;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

public class ErrorProxy implements InvocationHandler {

    @SuppressWarnings("unchecked")
    public static <T> T newProxyInstance(Class<T> klass) {
        return (T) Proxy.newProxyInstance(klass.getClassLoader(), new Class<?>[]{klass}, new ErrorProxy());
    }

    @Override
    public Object invoke(Object proxy, Method method, final Object[] args) throws Throwable {
        ErrorDef errorDef = method.getAnnotation(ErrorDef.class);
        if (errorDef == null) {
            return method.invoke(proxy, args);
        }

        final int httpStatusCode = errorDef.httpStatusCode();

        final String code;
        if (errorDef.code().isEmpty()) {
            code = null;
        } else {
            code = errorDef.code();
        }

        final String message;
        if (errorDef.message().isEmpty()) {
            message = null;
        } else if (args == null || args.length == 0) {
            message = errorDef.message();
        } else {
            message = MessageFormat.format(errorDef.message(), args);
        }

        final String field;
        if (errorDef.field().isEmpty()) {
            field = null;
        } else if (args == null || args.length == 0) {
            field = errorDef.field();
        } else {
            field = MessageFormat.format(errorDef.field(), args);
        }

        final List<AppError> causes;
        if (args != null && args.length > 0 && (args[args.length - 1] instanceof AppError[])) {
            AppError[] causesArray = (AppError[]) args[args.length - 1];
            if (causesArray == null || causesArray.length == 0) {
                causes = null;
            } else {
                causes = Arrays.asList(causesArray);
            }
        } else {
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
            public String getMessage() {
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
        };
    }
}
