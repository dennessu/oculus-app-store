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

/**
 * Created by haomin on 14-3-6.
 */
public class ShardAwareDaoProxy implements InvocationHandler {
    private Object target;
    private IdGeneratorFacade idGeneratorFacade;

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
                    if (args[i].getClass().equals(Long.class)) {
                        return Helper.getShardId((long)args[i]);
                    }
                    else {
                        throw new RuntimeException("@SeedParam annotation must be placed with Long type field, " +
                                "error with class " + args[i].getClass().getCanonicalName());
                    }
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
                Class<?> idClazz = arg.getClass();

                do {
                    for(Field idField : idClazz.getDeclaredFields()) {
                        Id idAnnotation = idField.getAnnotation(Id.class);
                        if (idAnnotation != null) {
                            Method idGetMethod = Helper.tryObtainGetterMethod(leafClazz, idField.getName(), Long.class);
                            Method idSetMethod = Helper.tryObtainSetterMethod(leafClazz, idField.getName(), Long.class);

                            if (idGetMethod == null || idSetMethod == null) {
                                throw new RuntimeException("@Id annotation must be placed with Long type field," +
                                        " and with proper getter and setter method available. "
                                        + idClazz.getCanonicalName());
                            }

                            if (idGetMethod.invoke(arg) == null) { //id not set yet
                                Class<?> seedIdClazz = leafClazz;
                                do {
                                    for(Field seedField : seedIdClazz.getDeclaredFields()) {
                                        SeedId seedIdAnnotation = seedField.getAnnotation(SeedId.class);
                                        if (seedIdAnnotation != null) {
                                            Method seedGetMethod = Helper.tryObtainGetterMethod(leafClazz,
                                                    seedField.getName(), Long.class);
                                            if (seedGetMethod == null) {
                                                throw new RuntimeException("@SeedId annotation must be placed with " +
                                                        "Long type field, and with proper getter method available. "
                                                        + seedIdClazz.getCanonicalName());
                                            }

                                            // Generate primary id on any shard if @SeedId
                                            // and @Id annotation on the same field
                                            if(seedField.equals(idField)) {
                                                long nextId = idGeneratorFacade.nextId(UserId.class);
                                                idSetMethod.invoke(arg, nextId);
                                                return Helper.getShardId(nextId);
                                            }
                                            else {  // use @SeedId field as id generator seed
                                                long seed = (long)seedGetMethod.invoke(arg);
                                                if (leafClazz.getCanonicalName()
                                                        .equalsIgnoreCase("com.junbo.order.db.entity.OrderEntity")) {
                                                    long nextId = idGeneratorFacade.nextId(OrderId.class, seed);
                                                    idSetMethod.invoke(arg, nextId);
                                                    return Helper.getShardId(nextId);
                                                }
                                                else {
                                                    long nextId = idGeneratorFacade.nextId(UserId.class, seed);
                                                    idSetMethod.invoke(arg, nextId);
                                                    return Helper.getShardId(nextId);
                                                }
                                            }
                                        }
                                    }
                                    seedIdClazz = seedIdClazz.getSuperclass();
                                } while (seedIdClazz != null);

                                // @SeedId not found, domain data in domaindata service?
                                // todo: haomin
                                throw new RuntimeException("@SeedId annotation not found on entity class "
                                        + leafClazz.getCanonicalName());
                            }
                            else {  // entity id has been set, not POST method
                                return Helper.getShardId((long)idField.get(arg));
                            }
                        }
                    }

                    idClazz = idClazz.getSuperclass();
                } while (idClazz != null);

                throw new RuntimeException("@Id annotation not found on entity class "
                        + leafClazz.getCanonicalName());
            }
        }

        throw new RuntimeException("Can't find any argument with @Entity annotation.");
    }
}
