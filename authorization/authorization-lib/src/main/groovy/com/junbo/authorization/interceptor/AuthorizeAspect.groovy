/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.interceptor

import com.junbo.authorization.AuthorizeCallback
import com.junbo.authorization.AuthorizeCallbackFactory
import com.junbo.authorization.annotation.AuthContextParam
import com.junbo.authorization.annotation.AuthorizeRequired
import com.junbo.authorization.service.AuthorizeService
import groovy.transform.CompileStatic
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.beans.BeansException
import org.springframework.beans.factory.annotation.Required
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

import java.lang.annotation.Annotation
import java.lang.reflect.Method

/**
 * AuthorizeAspect.
 */
@CompileStatic
@Aspect
class AuthorizeAspect implements ApplicationContextAware {
    private AuthorizeService authorizeService
    private ApplicationContext applicationContext

    @Required
    void setAuthorizeService(AuthorizeService authorizeService) {
        this.authorizeService = authorizeService
    }

    @Around('@annotation(requiredAnnotation)')
    Object doAuthorize(ProceedingJoinPoint joinPoint, AuthorizeRequired requiredAnnotation) {
        if (authorizeService.authorizeEnabled) {
            String methodName = joinPoint.signature.name
            MethodSignature methodSignature = (MethodSignature) joinPoint.signature
            Method method = methodSignature.method
            if (method.declaringClass.isInterface()) {
                method = joinPoint.target.class.getDeclaredMethod(methodName, method.parameterTypes)
            }

            Map<String, Object> map = [:]
            method.parameterAnnotations.eachWithIndex { Annotation[] annotations, int i ->
                if (annotations.length > 0) {
                    annotations.each { Annotation anno ->
                        if (anno instanceof AuthContextParam) {
                            AuthContextParam contextParam = (AuthContextParam) anno
                            map[contextParam.value()] = joinPoint.args[i]
                        }
                    }
                }
            }

            map['apiName'] = requiredAnnotation.apiName()
            AuthorizeCallbackFactory factoryBean = (AuthorizeCallbackFactory) applicationContext
                    .getBean(requiredAnnotation.authCallBackFactoryBean())
            AuthorizeCallback callback = factoryBean.create(map)
            authorizeService.authorize(callback)

            Object result = joinPoint.proceed()

            if (Collection.isAssignableFrom(result.class)) {
                Collection filteredResult = (Collection) result.class.newInstance()
                Collection resultCollection = (Collection) result
                resultCollection.each { Object entity ->
                    Object filtered = postFilter(requiredAnnotation, entity)
                    if (filtered != null) {
                        filteredResult.add(filtered)
                    }
                }

                return filteredResult
            }

            return postFilter(requiredAnnotation, result)
        }

        return joinPoint.proceed()
    }

    private Object postFilter(AuthorizeRequired annotation, Object entity) {
        Map<String, Object> map = [:]
        map['apiName'] = annotation.apiName()
        map['entity'] = entity

        AuthorizeCallbackFactory factoryBean = (AuthorizeCallbackFactory) applicationContext
                .getBean(annotation.authCallBackFactoryBean())
        AuthorizeCallback callback = factoryBean.create(map)

        authorizeService.authorize(callback)

        return callback.postFilter()
    }

    @Override
    void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext
    }
}
