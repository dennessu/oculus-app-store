/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.view

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.model.ViewModel
import groovy.transform.CompileStatic

/**
 * RedirectToPage.
 */
@CompileStatic
class ConsentView extends AbstractView {

    @Override
    protected Promise<ViewModel> buildViewModel(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        def model = new ViewModel(
                view: 'consent',
                model: [
                        'clientId': contextWrapper.client.clientId,
                        'userId'  : contextWrapper.loginState.userId,
                        'scopes'  : contextWrapper.oauthInfo.scopes
                ] as Map<String, Object>,
                errors: contextWrapper.errors.unique(new ErrorComparator()).asList()
        )

        return Promise.pure(model)
    }
}
