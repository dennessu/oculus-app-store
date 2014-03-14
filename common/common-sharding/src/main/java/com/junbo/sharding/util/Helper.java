/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.util;

import com.junbo.common.id.Id;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.Locale;

/**
 * Created by haomin on 14-3-14.
 */
public class Helper {
    private static ThreadLocal<Integer> currentShardId = new ThreadLocal<Integer>();

    private Helper() {}

    /**
     * return shardId value of id.
     * @param id
     * @return
     */
    public static int getShardId(Id id) {
        return getShardId(id.getValue());
    }

    /**
     * This shardId calculation is based on below Id format.
     *
     * Id generator proposed by oculus rift, it will use 48 bits
     * CCCC CCCC CCCC CCCC CCCC CCCC CCCC CCCC CCSS SSSS SSDD DDVV
     *    C is 34 bits of shuffled sequential ID
     *    S is 8 bits of shard ID
     *    D is 4 bits of data-center ID
     *    V is the ID version (hard coded 0)
     *    V is the least significant bits (hard coded 0 to indicate positive)
     *
     * order Id generator proposed by oculus rift, it will use 40 bits
     * 0CCC CCCC CCCC CCCC CCCC CCCC CCSS SSSS SSDD DDVV
     *    C is 25 bits of shuffled sequential ID
     *    S is 8 bits of shard ID
     *    D is 4 bits of data-center ID
     *    V is the ID version (hard coded 00)
     *    V is the least significant bits
     *
     * @param id
     * @return shardId
     */
    public static int getShardId(Long id) {
        int shardId = (int)((id >> 6) & 0xff);
        if (shardId < 0 || shardId > 255) {
            throw new RuntimeException("invalid shardId value: " + shardId);
        }

        return shardId;
    }

    public static void setCurrentShardId(int shardId) {
        currentShardId.set(shardId);
    }

    public static int getCurrentThreadLocalShardId() {
        if (currentShardId.get() == null) {
            throw new RuntimeException("current shardId hasn't been set.");
        }

        return currentShardId.get().intValue();
    }

    public static Method tryObtainGetterMethod(Class<?> clazz, final String propertyName, final Class<?> propertyType) {
        Assert.notNull(propertyName);
        Assert.isTrue(propertyName.length() > 1);

        final String upperPropertyName =
                propertyName.substring(0, 1).toUpperCase(Locale.ENGLISH) + propertyName.substring(1);

        // try getXxx method
        Method result = getFirstMethodByFilter(clazz, new Func<Method, Boolean>() {
            @Override
            public Boolean execute(Method method) {
                return method.getName().equals("get" + upperPropertyName) && method.getParameterTypes().length == 0
                        && (propertyType == null || method.getReturnType().equals(propertyType));
            }
        });

        if (result != null) {
            return result;
        }

        // try isXxx method
        result = getFirstMethodByFilter(clazz, new Func<Method, Boolean>() {
            @Override
            public Boolean execute(Method method) {
                return method.getName().equals("is" + upperPropertyName) && method.getParameterTypes().length == 0
                        && !method.getReturnType().equals(Void.TYPE);
            }
        });

        return result;
    }

    public static Method tryObtainSetterMethod(final Class<?> clazz, final String propertyName,
                                                final Class<?> propertyType) {
        Assert.notNull(propertyName);
        Assert.isTrue(propertyName.length() > 1);

        final String methodName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);

        // try setXxx method
        Method result = getFirstMethodByFilter(clazz, new Func<Method, Boolean>() {
            @Override
            public Boolean execute(Method method) {
                return method.getName().equals(methodName) && method.getParameterTypes().length == 1
                        && (propertyType == null || method.getParameterTypes()[0].equals(propertyType))
                        && method.getReturnType().equals(Void.TYPE);
            }
        });

        return result;
    }

    private static Method getFirstMethodByFilter(Class<?> clazz, Func<Method, Boolean> filter) {
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
