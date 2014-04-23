/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.util;

import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.Locale;

/**
 * Created by haomin on 14-4-9.
 */
public class Utils {
    private Utils() { }

    public static String combineUrl(String... urls) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < urls.length; ++i) {
            String url = urls[i];

            if (i != 0 && !url.startsWith("/")) {
                // Append an extra "/" if the current url part doesn't contain one.
                // Don't append the "/" before the first url component to keep urls relative or absolute
                result.append("/");
            }

            if (url.endsWith("/")) {
                // always omit the tailing "/" when appending url component
                result.append(url.substring(0, url.length() - 1));
            } else {
                result.append(url);
            }
        }
        return result.toString();
    }

    public static Method tryObtainGetterMethod(Class<?> clazz, final String propertyName) {
        Assert.notNull(propertyName);
        Assert.isTrue(propertyName.length() > 1);

        final String upperPropertyName =
                propertyName.substring(0, 1).toUpperCase(Locale.ENGLISH) + propertyName.substring(1);

        // try getXxx method
        Method result = fetchFirstMethodByFilter(clazz, new Func<Method, Boolean>() {
            @Override
            public Boolean execute(Method method) {
                return (method.getName().equals("get" + upperPropertyName) && method.getParameterTypes().length == 0)
                        && (method.getReturnType() != Object.class);
            }
        });

        if (result != null) {
            return result;
        }

        // try isXxx method
        result = fetchFirstMethodByFilter(clazz, new Func<Method, Boolean>() {
            @Override
            public Boolean execute(Method method) {
                return method.getName().equals("is" + upperPropertyName) && method.getParameterTypes().length == 0
                        && !method.getReturnType().equals(Void.TYPE);
            }
        });

        return result;
    }

    public static Method tryObtainSetterMethod(final Class<?> clazz, final String propertyName) {
        Assert.notNull(propertyName);
        Assert.isTrue(propertyName.length() > 1);

        final String methodName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);

        // try setXxx method
        Method result = fetchFirstMethodByFilter(clazz, new Func<Method, Boolean>() {
            @Override
            public Boolean execute(Method method) {
                return method.getName().equals(methodName) && method.getParameterTypes().length == 1
                        && method.getReturnType().equals(Void.TYPE);
            }
        });

        return result;
    }

    private static Method fetchFirstMethodByFilter(Class<?> clazz, Func<Method, Boolean> filter) {
        for (Method method : clazz.getMethods()) {
            if (filter.execute(method)) {
                return method;
            }
        }

        return null;
    }

    private interface Func<I, O> {
        O execute(I param);
    }
}
