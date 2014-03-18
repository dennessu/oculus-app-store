/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.core;

import com.junbo.common.id.OrderId;
import com.junbo.common.id.UserId;
import com.junbo.sharding.IdGeneratorFacade;
import com.junbo.sharding.annotations.SeedId;
import com.junbo.sharding.annotations.SeedParam;
import com.junbo.sharding.util.Helper;
import junit.framework.Assert;
import org.springframework.core.annotation.AnnotationUtils;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by haomin on 14-3-6.
 */
public class ShardAwareDaoProxy implements InvocationHandler {
    private Object target;
    private IdGeneratorFacade idGeneratorFacade;
    private Map<Class<?>, Method[]> accessMethodCache = new ConcurrentHashMap<>();

    public static <T> T newProxyInstance(Class<T> interfaceClass, Object target, IdGeneratorFacade idGeneratorFacade) {
        return (T)Proxy.newProxyInstance(target.getClass().getClassLoader(), new Class[]{interfaceClass},
                new ShardAwareDaoProxy(target, idGeneratorFacade));
    }

    private <T> ShardAwareDaoProxy(Object target, IdGeneratorFacade idGeneratorFacade) {
        this.target = target;
        this.idGeneratorFacade = idGeneratorFacade;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        int shardId = tryGetShardIdAndSetEntityId(method, args);
        Helper.setCurrentShardId(shardId);
        return method.invoke(target, args);
    }

    private int tryGetShardIdAndSetEntityId(Method mtd, Object[] args) throws Throwable {
        // @SeedParam processing
        Annotation[][] a = mtd.getParameterAnnotations();
        Assert.assertEquals(a.length, args.length);

        for (int i = 0; i < a.length; i++) {
            for (Annotation annotation : a[i]) {
                if (annotation instanceof SeedParam) {
                    return Helper.getShardId(args[i], args[i].getClass());
                }
            }
        }

        // @Entity arg processing
        for(Object arg : args) {
            Entity entityAnnotation = AnnotationUtils.findAnnotation(arg.getClass(), Entity.class);
            // if it is an entity class
            if (entityAnnotation != null) {
                for(Method method : arg.getClass().getMethods()) {
                    if (AnnotationUtils.findAnnotation(method, Id.class) != null) {
                        throw new RuntimeException("Sharding restrict @Id annotation usage as a Field annotation," +
                                " ShardAwareDaoProxy will rely on @Id to do implicit id generation. " +
                                arg.getClass().getCanonicalName() + " violate this rule, please fix!");
                    }
                }
                Class<?> leafClazz = arg.getClass();

                Method[] methods = accessMethodCache.get(leafClazz);
                if (methods != null) {
                    return setIdAndGetShardId(arg, methods);
                }

                // methods[0]: @Id getter method
                // methods[1]: @Id setter method
                // methods[2]: @seedId getter method, null if @Id and @SeedId refer to the same field
                methods = new Method[3];
                accessMethodCache.put(leafClazz, methods);

                Class<?> idClazz = arg.getClass();
                do {
                    for(Field idField : idClazz.getDeclaredFields()) {
                        Id idAnnotation = idField.getAnnotation(Id.class);
                        if (idAnnotation != null) {
                            Method idGetMethod = Helper.tryObtainGetterMethod(leafClazz, idField.getName());
                            Method idSetMethod = Helper.tryObtainSetterMethod(leafClazz, idField.getName());

                            if (idGetMethod == null || idSetMethod == null) {
                                throw new RuntimeException("@Id annotation must be placed with Long type field," +
                                        " and with proper getter and setter method available. "
                                        + idClazz.getCanonicalName());
                            }
                            methods[0] = idGetMethod;
                            methods[1] = idSetMethod;

                            if (idGetMethod.invoke(arg) == null) { //id not set yet
                                Class<?> seedIdClazz = leafClazz;
                                do {
                                    for(Field seedField : seedIdClazz.getDeclaredFields()) {
                                        SeedId seedIdAnnotation = seedField.getAnnotation(SeedId.class);
                                        if (seedIdAnnotation != null) {
                                            Method seedGetMethod = Helper.tryObtainGetterMethod(leafClazz,
                                                    seedField.getName());
                                            if (seedGetMethod == null) {
                                                throw new RuntimeException("@SeedId annotation must be placed with " +
                                                        "Long type field, and with proper getter method available. "
                                                        + seedIdClazz.getCanonicalName());
                                            }

                                            if (!seedField.equals(idField)) {
                                                methods[2] = seedGetMethod;
                                            }

                                            return setIdAndGetShardId(arg, methods);
                                        }
                                    }
                                    seedIdClazz = seedIdClazz.getSuperclass();
                                } while (seedIdClazz != null);

                                // @SeedId not found
                                throw new RuntimeException("@SeedId annotation not found on entity class "
                                        + leafClazz.getCanonicalName());
                            }
                            else {  // entity id has been set, not POST method
                                return setIdAndGetShardId(arg, methods);
                            }
                        }
                    }

                    idClazz = idClazz.getSuperclass();
                } while (idClazz != null);

                throw new RuntimeException("@Id annotation not found on entity class "
                        + leafClazz.getCanonicalName());
            }
        }

        throw new RuntimeException("Can't find any argument with @Entity or @SeedParam annotation.");
    }

    private int setIdAndGetShardId(Object entity, Method[] methods) throws Throwable{
        Method idGetMethod = methods[0];
        Method idSetMethod = methods[1];
        Method seedIdGetMethod = methods[2];

        if (idGetMethod != null) {
            if (idGetMethod.invoke(entity) != null) {
                return Helper.getShardId(idGetMethod.invoke(entity), idGetMethod.getReturnType());
            }
            else if (seedIdGetMethod == null) {
                long nextId = idGeneratorFacade.nextId(UserId.class);
                idSetMethod.invoke(entity, nextId);
                return Helper.getShardId(nextId, Long.class);
            }
            else {
                long seed = (Long)seedIdGetMethod.invoke(entity);
                if (entity.getClass().getCanonicalName()
                        .equalsIgnoreCase("com.junbo.order.db.entity.OrderEntity")) {
                    long nextId = idGeneratorFacade.nextId(OrderId.class, seed);
                    idSetMethod.invoke(entity, nextId);
                    return Helper.getShardId(nextId, Long.class);
                }
                else {
                    long nextId = idGeneratorFacade.nextId(UserId.class, seed);
                    idSetMethod.invoke(entity, nextId);
                    return Helper.getShardId(nextId, Long.class);
                }
            }
        }

        throw new RuntimeException("idGetterMethod is null!");
    }
}
