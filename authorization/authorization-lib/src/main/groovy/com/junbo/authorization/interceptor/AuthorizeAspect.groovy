/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.interceptor

import com.junbo.authorization.AuthorizeCallback
import com.junbo.authorization.annotation.AuthorizeRequired
import com.junbo.authorization.annotation.AuthContextParam
import com.junbo.authorization.model.AuthorizeContext
import com.junbo.authorization.service.AuthorizeService
import groovy.transform.CompileStatic
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.beans.factory.annotation.Required

import java.lang.annotation.Annotation
import java.lang.reflect.Method

/**
 * AuthorizeAspect.
 */
@CompileStatic
@Aspect
class AuthorizeAspect {
    private AuthorizeService authorizeService

    @Required
    void setAuthorizeService(AuthorizeService authorizeService) {
        this.authorizeService = authorizeService
    }

    @Around('@annotation(requiredAnnotation)')
    Object doAuthorize(ProceedingJoinPoint joinPoint, AuthorizeRequired requiredAnnotation) {
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
        AuthorizeCallback callback = (AuthorizeCallback) requiredAnnotation.authCallBack().newInstance(map)
        Set<String> claims = authorizeService.getClaims(callback)
        AuthorizeContext.CLAIMS.set(claims)
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

    private Object postFilter(AuthorizeRequired annotation, Object entity) {
        Map<String, Object> map = [:]
        map['apiName'] = annotation.apiName()
        map['entity'] = entity
        AuthorizeCallback callback = (AuthorizeCallback) annotation.authCallBack().newInstance(map)
        Set<String> claims = authorizeService.getClaims(callback)
        AuthorizeContext.CLAIMS.set(claims)

        return callback.postFilter()
    }
}
