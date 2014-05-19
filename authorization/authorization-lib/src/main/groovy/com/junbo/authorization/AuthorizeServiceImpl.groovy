/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization

import com.junbo.common.error.AppErrorException
import com.junbo.oauth.spec.endpoint.ApiDefinitionEndpoint
import com.junbo.oauth.spec.model.ApiDefinition
import com.junbo.oauth.spec.model.MatrixRow
import groovy.transform.CompileStatic
import net.sf.ehcache.Ehcache
import net.sf.ehcache.Element
import org.springframework.beans.factory.annotation.Required

/**
 * AuthorizeServiceImpl.
 */
@CompileStatic
class AuthorizeServiceImpl implements AuthorizeService {

    private ApiDefinitionEndpoint apiDefinitionEndpoint

    private Ehcache apiDefinitionCache

    private Map<String, ConditionEvaluator> conditionEvaluators

    @Required
    void setApiDefinitionEndpoint(ApiDefinitionEndpoint apiEndpoint) {
        this.apiDefinitionEndpoint = apiEndpoint
    }

    @Required
    void setApiDefinitionCache(Ehcache apiDefinitionCache) {
        this.apiDefinitionCache = apiDefinitionCache
    }

    @Required
    void setConditionEvaluators(List<ConditionEvaluator> conditionEvaluators) {

        this.conditionEvaluators = new HashMap<>()

        for (ConditionEvaluator evaluator : conditionEvaluators) {
            this.conditionEvaluators.put(evaluator.scriptType, evaluator)
        }
    }

    @Override
    public Set<String> authorize(AuthorizeCallback callback) {
        ApiDefinition api = getApiDefinition(callback.apiName)

        if (api == null) {
            return Collections.emptySet()
        }

        Set<String> rights = []

        for (String scopeName : api.scopes.keySet()) {
            if (!AuthorizeContext.hasScopes(scopeName)) {
                continue
            }

            List<MatrixRow> matrixRows = api.scopes[scopeName]

            for (MatrixRow row : matrixRows) {
                def conditionEvaluator = conditionEvaluators.get(row.scriptType)
                if (!conditionEvaluator) {
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

        return rights
    }

    private ApiDefinition getApiDefinition(String apiName) {

        Element cachedElement = apiDefinitionCache.get(apiName)
        if (cachedElement != null) {
            return (ApiDefinition) cachedElement.objectValue
        }

        try {
            def apiDefinition = apiDefinitionEndpoint.get(apiName).get()
            apiDefinitionCache.put(new Element(apiName, apiDefinition))

            return apiDefinition
        } catch (AppErrorException ex) {
            if (ex.error.httpStatusCode == 404) {
                return null
            } else {
                throw new RuntimeException("Failed to get api definition for $apiName", ex)
            }
        }
    }
}
