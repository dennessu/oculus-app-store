/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.core;

import com.junbo.langur.core.promise.Promise;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by haomin on 14-3-6.
 */
public class ShardAwareDaoProxy implements InvocationHandler {
    private Object target;

    public static Object newProxyInstance(Class<?> interfaceClass, Object target) {
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), new Class[]{interfaceClass},
                new ShardAwareDaoProxy(target));
    }

    private ShardAwareDaoProxy(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Promise.currentContext().set("shardId", "0");
        return method.invoke(target, args);
    }
}
