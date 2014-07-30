/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization

import com.junbo.common.error.AppCommonErrors
import com.junbo.langur.core.rest.ResourceScopeValidator
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert

/**
 * ResourceScopeValidatorImpl.
 */
@CompileStatic
class ResourceScopeValidatorImpl implements ResourceScopeValidator {
    private Map<String, String[]> apiScopeMapping

    private Boolean disabled

    @Required
    void setApiScopeMapping(Map<String, String> apiScopeMapping) {
        this.apiScopeMapping = [:]
        for (String key : apiScopeMapping.keySet()) {
            this.apiScopeMapping.put(key, apiScopeMapping.get(key).split(' '))
        }
    }

    @Required
    void setDisabled(Boolean disabled) {
        this.disabled = disabled
    }

    @Override
    void validateScope(String apiName) {
        if (disabled) {
            return
        }

        String[] scopes = apiScopeMapping.get(apiName)

        if (scopes == null) {
            String[] tokens = apiName.split('\\.')
            Assert.isTrue(tokens.length == 2)

            scopes = apiScopeMapping.get(tokens[0])
        }

        if (scopes == null) {
            throw AppCommonErrors.INSTANCE.forbiddenWithMessage("The $apiName is not ready for external call")
                    .exception()
        }

        if (!AuthorizeContext.hasAnyScope(scopes)) {
            throw AppCommonErrors.INSTANCE.insufficientScope(apiName).exception()
        }
    }
}
