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
 * EmailVerifyView.
 */
@CompileStatic
class EmailVerifyView extends AbstractView {
    @Override
    protected Promise<ViewModel> buildViewModel(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        def model = new ViewModel(
                view: 'emailVerify',
                model: [
                        'verifyResult': contextWrapper.errors.isEmpty() ? 'true' : 'false'
                ] as Map<String, Object>,
                errors: contextWrapper.errors.unique(new ErrorComparator()).asList()
        )

        return Promise.pure(model)
    }
}
