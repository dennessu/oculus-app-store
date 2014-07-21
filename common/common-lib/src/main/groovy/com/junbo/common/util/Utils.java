/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.util;

import com.junbo.common.id.Id;
import com.junbo.configuration.topo.DataCenters;
import groovy.lang.Closure;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by haomin on 14-4-9.
 */
public class Utils {
    private Utils() { }

    public static String safeToString(Object obj) {
        if (obj == null) {
            return null;
        }
        return obj.toString();
    }

    public static <V> V with(AutoCloseable closable, Closure<V> closure) throws Exception {
        try (AutoCloseable scope = closable) {
            return closure.call();
        }
    }

    /**
     * The helper function to filter configuration values for current data center.
     *
     * The per DC configuration is in the following format:
     *      {config0};dc0, {config1};dc1
     * This function will return {config0} if current DC is dc0.
     *
     * Note that the config value part cannot contain , or ;
     *
     * @param allDcValues the configuration in all dcs.
     * @return the configuration in current dc.
     */
    public static String filterPerDataCenterConfig(String allDcValues, String configName) {
        return parsePerDataCenterConfig(allDcValues, configName).get(DataCenters.instance().currentDataCenter());
    }

    /**
     * The helper function to parse configuration values for current data center.
     *
     * The per DC configuration is in the following format:
     *      {config0};dc0, {config1};dc1
     * This function will return a map from dc to config value.
     *
     * Note that the config value part cannot contain , or ;
     *
     * @param allDcValues the configuration in all dcs.
     * @return the configurations per dc.
     */
    public static Map<String, String> parsePerDataCenterConfig(String allDcValues, String configName) {
        Map<String, String> result = new HashMap<>();

        String[] dcValues = allDcValues.split(",");
        for (String dcValue : dcValues) {
            dcValue = dcValue.trim();
            String[] dcValueComponents = dcValue.split(";");
            if (dcValueComponents.length != 2) {
                throw new RuntimeException(String.format("Invalid %s: %s item: %s", configName, allDcValues, dcValue));
            }
            String value = dcValueComponents[0];
            String dc = dcValueComponents[1];

            if (!DataCenters.instance().hasDataCenter(dc)) {
                throw new RuntimeException("Unknown datacenter: " + dc + " in config: " + configName + " value: " + allDcValues);
            }
            if (result.containsKey(dc)) {
                throw new RuntimeException(String.format("Duplicated %s for DC '%s': %s, %s", configName, dc, value, result));
            }
            result.put(dc, value);
        }
        return result;
    }

    public static String combineUrl(String... urls) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < urls.length; ++i) {
            String url = urls[i];
            if (url == null) continue;

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

    public static Long keyToLong(Object key) {
        if (key == null) return null;

        if (key instanceof Id) {
            return ((Id)key).getValue();
        } else if (key instanceof Long) {
            return (Long)key;
        }
        throw new RuntimeException("Unknown key type: " + key.getClass());
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
