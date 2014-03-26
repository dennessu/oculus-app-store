/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.test

import com.fasterxml.jackson.databind.ObjectMapper
import com.junbo.authorization.annotation.AuthorizeRequired
import com.junbo.authorization.annotation.ContextParam
import com.junbo.authorization.model.AccessToken
import com.junbo.authorization.model.AuthorizeContext
import com.junbo.authorization.model.Role
import com.junbo.authorization.model.Scope
import groovy.transform.CompileStatic
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.springframework.expression.ExpressionParser
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext

/**
 * EntityServiceImpl.
 */
@CompileStatic
class EntityServiceImpl implements EntityService {
    @Override
    @AuthorizeRequired(authCallBack = EntityAuthorizeCallback)
    Entity get(
            @ContextParam('id') Long id,
            @ContextParam('accessToken') AccessToken accessToken, AuthorizeContext context) {
        if (context.claims.contains('read')) {
            return new Entity(id: id, name: 'name', createdBy: 'system')
        }
        return null
    }

    Boolean isOwner() {
        return true
    }

    Boolean isAdmin() {
        return false
    }
    static void main(String[] args) {
//        ObjectMapper objectMapper = new ObjectMapper()
//        ApplicationContext context = new ClassPathXmlApplicationContext('spring/context.xml')
//        EntityService service = (EntityService) context.getBean('service')
//        Scope scope = new Scope()
//        Map<Role, Set<String>> scopeClaims = [:]
//        scopeClaims[Role.OWNER] = ['read', 'owner'].toSet()
//        scopeClaims[Role.ADMIN] = ['read', 'admin'].toSet()
//        scopeClaims[Role.GUEST] = ['read'].toSet()
//        scope.claims = scopeClaims
//
//        Entity entity = service.get(456L, new AccessToken(tokenValue: 'test', userId: 456L, scopes: [scope].toSet()), null)
//        println objectMapper.writeValueAsString(entity)
//        entity = service.get(456L, new AccessToken(tokenValue: 'test', userId: 789L, scopes: [scope].toSet()), null)
//        println objectMapper.writeValueAsString(entity)
//        entity = service.get(456L, new AccessToken(tokenValue: 'test', userId: 123L, scopes: [scope].toSet()), null)
//        println objectMapper.writeValueAsString(entity)
        EntityServiceImpl entityService = new EntityServiceImpl()
        StandardEvaluationContext context = new StandardEvaluationContext(entityService)
        ExpressionParser parser = new SpelExpressionParser();
        Boolean is = parser.parseExpression('owne. && adm').getValue(context, Boolean)
        println is
    }
}
