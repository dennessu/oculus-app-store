/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.interceptor

import com.junbo.authorization.AuthorizeCallback
import com.junbo.authorization.annotation.AuthorizeRequired
import com.junbo.authorization.annotation.ContextParam
import com.junbo.authorization.model.AccessToken
import com.junbo.authorization.model.AuthorizeContext
import com.junbo.authorization.model.Role
import com.junbo.authorization.model.Scope
import groovy.transform.CompileStatic
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature

import java.lang.annotation.Annotation
import java.lang.reflect.Method

/**
 * AuthorizeAspect.
 */
@CompileStatic
@Aspect
class AuthorizeAspect {
    //@Around('execution(* *.printSomething())')
    @Around('@annotation(annotation)')
    Object doAuthorize(ProceedingJoinPoint joinPoint, AuthorizeRequired annotation) {
        final String methodName = joinPoint.getSignature().getName();
        final MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        if (method.getDeclaringClass().isInterface()) {
            method = joinPoint.getTarget().getClass().getDeclaredMethod(methodName, method.getParameterTypes());
        }

        Map<String, Object> map = [:]
        method.getParameterAnnotations().eachWithIndex { Annotation[] annotations, int i ->
            if (annotations.length > 0) {
                annotations.each { Annotation anno ->
                    if (anno instanceof ContextParam) {
                        ContextParam contextParam = (ContextParam) anno
                        map[contextParam.value()] = joinPoint.getArgs()[i]
                    }
                }
            }
        }

        AuthorizeCallback callback = (AuthorizeCallback) annotation.authCallBack().newInstance()
        AuthorizeContext context = callback.newContext(map)

        AccessToken accessToken = (AccessToken) map['accessToken']

        context.claims = getClaims(accessToken, callback, context)
        Object[] arguments = new Object[method.parameterTypes.length]
        for (int i = 0; i < joinPoint.getArgs().length; i++) {
            arguments[i] = joinPoint.getArgs()[i]
        }
        arguments[arguments.length - 1] = context
        Object object = joinPoint.proceed(arguments)

        context = callback.newContext(object)
        context.context['accessToken'] = accessToken
        context.claims = getClaims(accessToken, callback, context)

        return callback.postFilter(object, context)
        // if object is a collection, handle collection
    }

    static Set<String> getClaims(AccessToken accessToken, AuthorizeCallback callback, AuthorizeContext context) {
        Set<String> claims = []

        accessToken.scopes.each { Scope scope ->
            scope.claims.keySet().each { Role role ->
                if (callback.hasRole(role, context)) {
                    claims.addAll(scope.claims[role])
                }
            }
        }
        return claims
    }
}
