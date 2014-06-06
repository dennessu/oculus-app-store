/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.data.handler

import com.junbo.common.error.AppErrorException
import com.junbo.langur.core.client.TypeReference
import com.junbo.oauth.spec.endpoint.ScopeEndpoint
import com.junbo.oauth.spec.model.Scope
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * ScopeDataHandler.
 */
@CompileStatic
class ScopeDataHandler extends BaseDataHandler {
    private ScopeEndpoint scopeEndpoint

    @Required
    void setScopeEndpoint(ScopeEndpoint scopeEndpoint) {
        this.scopeEndpoint = scopeEndpoint
    }

    @Override
    void handle(String content) {
        Scope scope = null

        try {
            scope = transcoder.decode(new TypeReference<Scope>() {}, content) as Scope
        } catch (Exception e) {
            logger.warn('Error parsing Scope, skip this content', e)
            return
        }

        Scope existing = null
        try {
            existing = scopeEndpoint.getScope(scope.name).get()
        } catch (AppErrorException e) {
            logger.debug('This content does not exist in current database', e)
        }

        if (existing != null) {
            if (alwaysOverwrite) {
                logger.debug("Overwrite Scope $existing.name with this content")
                scope.revision = existing.revision
                scopeEndpoint.putScope(scope.name, scope)
            } else {
                logger.debug("The Scope $scope.name exists, skip this content")
            }
        } else {
            logger.debug("Create new Scope $scope.name with this content")
            scopeEndpoint.postScope(scope)
        }
    }
}
