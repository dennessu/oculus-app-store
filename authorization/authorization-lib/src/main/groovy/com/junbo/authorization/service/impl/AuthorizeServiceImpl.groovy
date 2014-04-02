/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.service.impl

import com.junbo.authorization.AuthorizeCallback
import com.junbo.authorization.model.AuthorizeContext
import com.junbo.authorization.service.AuthorizeService
import com.junbo.authorization.service.ConditionEvaluator
import com.junbo.oauth.spec.endpoint.ApiEndpoint
import com.junbo.oauth.spec.endpoint.TokenEndpoint
import com.junbo.oauth.spec.endpoint.TokenInfoEndpoint
import com.junbo.oauth.spec.model.*
import groovy.transform.CompileStatic
import org.springframework.beans.BeansException
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Required
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.util.Assert

import javax.ws.rs.core.HttpHeaders

/**
 * AuthorizeServiceImpl.
 */
@CompileStatic
class AuthorizeServiceImpl implements AuthorizeService, ApplicationContextAware, InitializingBean {
    private static final String AUTHORIZATION_HEADER = 'Authorization'
    private static final String API_SCOPE = 'api.info'
    private static final int TOKENS_LENGTH = 2
    private ApplicationContext applicationContext

    private TokenEndpoint tokenEndpoint

    private TokenInfoEndpoint tokenInfoEndpoint

    private ApiEndpoint apiEndpoint

    private final Map<String, ApiDefinition> apiDefinitions = [:]

    private ConditionEvaluator conditionEvaluator

    private String serviceClientId

    private String serviceClientSecret

    private Boolean authorizeEnabled

    @Required
    void setTokenEndpoint(TokenEndpoint tokenEndpoint) {
        this.tokenEndpoint = tokenEndpoint
    }

    @Required
    void setTokenInfoEndpoint(TokenInfoEndpoint tokenInfoEndpoint) {
        this.tokenInfoEndpoint = tokenInfoEndpoint
    }

    @Required
    void setApiEndpoint(ApiEndpoint apiEndpoint) {
        this.apiEndpoint = apiEndpoint
    }

    @Required
    void setConditionEvaluator(ConditionEvaluator conditionEvaluator) {
        this.conditionEvaluator = conditionEvaluator
    }

    @Required
    void setServiceClientId(String serviceClientId) {
        this.serviceClientId = serviceClientId
    }

    @Required
    void setServiceClientSecret(String serviceClientSecret) {
        this.serviceClientSecret = serviceClientSecret
    }

    @Override
    Boolean getAuthorizeEnabled() {
        return authorizeEnabled
    }

    @Required
    void setAuthorizeEnabled(Boolean authorizeEnabled) {
        this.authorizeEnabled = authorizeEnabled
    }

    @Override
    Set<String> getClaims(AuthorizeCallback callback) {
        String accessToken = parseAccessToken()

        TokenInfo tokenInfo = tokenInfoEndpoint.getTokenInfo(accessToken).wrapped().get()
        Set<String> tokenScopes = []
        tokenScopes.addAll(tokenInfo.scopes.split(' '))

        ApiDefinition api = apiDefinitions.get(callback.apiName)
        Collection<String> scopes = api.authorizePolicies.keySet().intersect(tokenScopes)

        callback.tokenInfo = tokenInfo

        Set<String> claimSet = []
        scopes.each { String scopeName ->
            AuthorizePolicy policy = api.authorizePolicies[scopeName]
            policy.claims.each { String condition, Set<String> claims ->
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

    @Override
    void afterPropertiesSet() throws Exception {
        if (authorizeEnabled) {
            AccessTokenResponse token = tokenEndpoint.postToken(serviceClientId,  serviceClientSecret,
                    GrantType.CLIENT_CREDENTIALS.name(), null, API_SCOPE, null, null, null, null, null).wrapped().get()

            List<ApiDefinition> apis = apiEndpoint.getAllApis("Bearer $token.accessToken").wrapped().get()

            apis.each { ApiDefinition api ->
                apiDefinitions[api.apiName] = api
            }
        }

        AuthorizeContext.authorizedEnabled = authorizeEnabled
    }
}
