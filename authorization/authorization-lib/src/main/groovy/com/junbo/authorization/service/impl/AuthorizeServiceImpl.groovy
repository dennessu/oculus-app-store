/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.service.impl

import com.junbo.authorization.AuthorizeCallback
import com.junbo.authorization.model.ApiDefinition
import com.junbo.authorization.model.Scope
import com.junbo.authorization.service.AuthorizeService
import com.junbo.authorization.service.ConditionEvaluator
import com.junbo.oauth.spec.endpoint.TokenInfoEndpoint
import com.junbo.oauth.spec.model.TokenInfo
import com.junbo.oauth.spec.model.TokenType
import groovy.transform.CompileStatic
import org.springframework.beans.BeansException
import org.springframework.beans.factory.annotation.Required
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.util.Assert

import javax.ws.rs.core.HttpHeaders

/**
 * AuthorizeServiceImpl.
 */
@CompileStatic
class AuthorizeServiceImpl implements AuthorizeService, ApplicationContextAware {
    private static final String AUTHORIZATION_HEADER = 'Authorization'
    private static final int TOKENS_LENGTH = 2
    private ApplicationContext applicationContext

    private TokenInfoEndpoint tokenInfoEndpoint

    private Map<String, ApiDefinition> apiDefinitions

    private ConditionEvaluator conditionEvaluator

    @Required
    void setTokenInfoEndpoint(TokenInfoEndpoint tokenInfoEndpoint) {
        this.tokenInfoEndpoint = tokenInfoEndpoint
    }

    @Required
    void setApiDefinitions(Map<String, ApiDefinition> apiDefinitions) {
        this.apiDefinitions = apiDefinitions
    }

    @Required
    void setConditionEvaluator(ConditionEvaluator conditionEvaluator) {
        this.conditionEvaluator = conditionEvaluator
    }

    @Override
    Set<String> getClaims(AuthorizeCallback callback) {
        String accessToken = parseAccessToken()

        TokenInfo tokenInfo = tokenInfoEndpoint.getTokenInfo(accessToken).wrapped().get()
        Set<String> tokenScopes = []
        tokenScopes.addAll(tokenInfo.scopes.split(' '))

        ApiDefinition api = apiDefinitions.get(callback.apiName)
        Collection<String> scopes = api.availableScopes.keySet().intersect(tokenScopes)

        callback.tokenInfo = tokenInfo

        Set<String> claimSet = []
        scopes.each { String scopeName ->
            Scope scope = api.availableScopes[scopeName]
            scope.claims.each { String condition, Set<String> claims ->
                if (conditionEvaluator.evaluate(condition, callback)) {
                    claimSet.addAll(claims)
                }
            }
        }

        return claimSet
    }

    private String parseAccessToken() {
        HttpHeaders httpHeaders = applicationContext.getBean(HttpHeaders)
        String authorization = httpHeaders.requestHeaders.getFirst(AUTHORIZATION_HEADER)
        String accessToken = extractAccessToken(authorization)
        return accessToken
    }

    @Override
    void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext
    }

    static String extractAccessToken(String authorization) {
        Assert.notNull(authorization, 'authorization is null')

        String[] tokens = authorization.split(' ')
        if (tokens.length != TOKENS_LENGTH || !tokens[0].equalsIgnoreCase(TokenType.BEARER.name())) {
            return null
        }

        return tokens[1]
    }
}
