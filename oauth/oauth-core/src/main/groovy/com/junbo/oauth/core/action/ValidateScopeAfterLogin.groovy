/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.authorization.ConditionEvaluator
import com.junbo.common.error.AppCommonErrors
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.db.repo.ScopeRepository
import com.junbo.oauth.spec.model.Scope
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert
import org.springframework.util.StringUtils

/**
 * ValidateScopeAfterLogin.
 */
@CompileStatic
class ValidateScopeAfterLogin implements Action {
    private ScopeRepository scopeRepository

    private boolean isAuthorizeFlow

    private ConditionEvaluator conditionEvaluator

    private ScopePreconditionFactory scopePreconditionFactory

    @Required
    void setScopeRepository(ScopeRepository scopeRepository) {
        this.scopeRepository = scopeRepository
    }

    @Required
    void setIsAuthorizeFlow(boolean isAuthorizeFlow) {
        this.isAuthorizeFlow = isAuthorizeFlow
    }

    @Required
    void setConditionEvaluator(ConditionEvaluator conditionEvaluator) {
        this.conditionEvaluator = conditionEvaluator
    }

    @Required
    void setScopePreconditionFactory(ScopePreconditionFactory scopePreconditionFactory) {
        this.scopePreconditionFactory = scopePreconditionFactory
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def oauthInfo = contextWrapper.oauthInfo
        def loginState = contextWrapper.loginState

        Assert.notNull(oauthInfo, 'oauthInfo is null')
        Assert.notNull(loginState, 'loginState is null')

        List<Scope> scopes = []
        oauthInfo.scopes.each { String scopeString ->
            Scope scope = scopeRepository.getScope(scopeString)
            if (scope != null) {
                scopes.add(scope)
            }
        }

        boolean tfaRequired = false
        boolean validationConditionRequired = false
        scopes.each { Scope scope ->
            if (scope.tfaRequired) {
                tfaRequired = true
            }

            if (StringUtils.hasText(scope.validationCondition)) {
                validationConditionRequired = true
            }
        }

        if (validationConditionRequired) {
            Boolean allowed = true
            StringBuilder forbiddenScopes = new StringBuilder()
            for (Scope scope : scopes) {
                if (StringUtils.hasText(scope.validationCondition)) {
                    def scopePrecondition = scopePreconditionFactory.create(contextWrapper)
                    if (!conditionEvaluator.evaluate(scope.validationCondition, scopePrecondition)) {
                        allowed = false
                        forbiddenScopes.append(scope.name)
                        forbiddenScopes.append(' ')
                    }
                }
            }

            if (allowed) {
                return Promise.pure(new ActionResult('success'))
            } else {
                if (!isAuthorizeFlow) {
                    throw AppCommonErrors.INSTANCE
                            .forbiddenWithMessage("The user does not match the " +
                            "pre-condition for scopes $forbiddenScopes").exception()
                } else {
                    contextWrapper.errors.add(AppCommonErrors.INSTANCE.
                            forbiddenWithMessage("The user does not match the " +
                                    "pre-condition for scopes $forbiddenScopes").error())
                    return Promise.pure(new ActionResult('forbidden'))
                }
            }
        }

        if (tfaRequired && isAuthorizeFlow) {
            return Promise.pure(new ActionResult('tfaRequired'))
        }

        return Promise.pure(new ActionResult('success'))
    }
}
