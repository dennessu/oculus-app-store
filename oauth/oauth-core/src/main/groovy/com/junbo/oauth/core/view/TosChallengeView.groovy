/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.view

import com.junbo.common.id.TosId
import com.junbo.common.id.UserId
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.spec.model.ViewModel
import groovy.transform.CompileStatic

/**
 * TosChallengeView.
 */
@CompileStatic
class TosChallengeView extends AbstractView {
    @Override
    protected Promise<ViewModel> buildViewModel(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        def modelMap = [
                'clientId': contextWrapper.client.clientId,
                'userId'  : new UserId(contextWrapper.loginState?.userId),
                'locale': contextWrapper.viewLocale,
                'tosChallenge': contextWrapper.tosChallenge.collect { String tosId ->
                    return new TosId(tosId)
                }
        ]

        def model = new ViewModel(
                view: 'tosChallenge',
                model: modelMap as Map<String, Object>,
                errors: contextWrapper.errors.unique(errorComparator).asList()
        )

        return Promise.pure(model)
    }
}
