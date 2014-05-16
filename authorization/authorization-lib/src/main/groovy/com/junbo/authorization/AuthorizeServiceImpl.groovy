/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization

import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.spec.endpoint.ApiDefinitionEndpoint
import com.junbo.oauth.spec.model.MatrixRow
import com.junbo.oauth.spec.model.ApiDefinition
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * AuthorizeServiceImpl.
 */
@CompileStatic
class AuthorizeServiceImpl implements AuthorizeService {

    private ApiDefinitionEndpoint apiDefinitionEndpoint

    private Map<String, ConditionEvaluator> conditionEvaluators

    private TokenInfoParser tokenInfoParser

    private Boolean useDummyRights

    @Required
    void setApiDefinitionEndpoint(ApiDefinitionEndpoint apiEndpoint) {
        this.apiDefinitionEndpoint = apiEndpoint
    }

    @Required
    void setConditionEvaluators(List<ConditionEvaluator> conditionEvaluators) {

        this.conditionEvaluators = new HashMap<>()

        for (ConditionEvaluator evaluator : conditionEvaluators) {
            this.conditionEvaluators.put(evaluator.scriptType, evaluator)
        }
    }

    @Required
    void setUseDummyRights(Boolean useDummyRights) {
        this.useDummyRights = useDummyRights
    }

    @Required
    void setTokenInfoParser(TokenInfoParser tokenInfoParser) {
        this.tokenInfoParser = tokenInfoParser
    }

    @Override
    public Promise<Set<String>> authorize(AuthorizeCallback callback) {

        if (useDummyRights) {
            return Promise.pure([AuthorizeContext.SUPER_RIGHT] as Set)
        }

        return tokenInfoParser.parseAndThen {
            return getApiDefinition(callback.apiName).then { ApiDefinition api ->
                if (api == null) {
                    throw new RuntimeException("api ${callback.apiName} not found")
                }

                def tokenScopes = AuthorizeContext.currentScopes as Set<String>
                def scopes = api.scopes.keySet().intersect(tokenScopes)

                Set<String> rights = []

                for (String scopeName : scopes) {
                    List<MatrixRow> matrixRows = api.scopes[scopeName]

                    for (MatrixRow row : matrixRows) {
                        def conditionEvaluator = conditionEvaluators.get(row.scriptType)
                        if (conditionEvaluator) {
                            throw new RuntimeException("scriptType ${row.scriptType} not supported")
                        }

                        if (conditionEvaluator.evaluate(row.precondition, callback)) {
                            rights.addAll(row.rights)

                            if (row.breakOnMatch) {
                                break
                            }
                        }
                    }
                }

                return Promise.pure(rights)
            }
        }
    }

    @Override
    public <T> Promise<T> authorizeAndThen(AuthorizeCallback<T> callback, Closure<Promise> closure) {
        return authorize(callback).then { Set<String> rights ->
            return RightsScope.with(rights, closure)
        }
    }

    @Override
    public <T> Promise<T> authorizeAndThen(AuthorizeCallback<T> callback, Promise.Func0<Promise<T>> func) {
        return authorize(callback).then { Set<String> rights ->
            return RightsScope.with(rights, func)
        }
    }

    private Promise<ApiDefinition> getApiDefinition(String apiName) {
        return apiDefinitionEndpoint.get(apiName)
    }
}
