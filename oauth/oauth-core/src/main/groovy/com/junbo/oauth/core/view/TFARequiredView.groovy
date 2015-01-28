/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.view

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.spec.model.ViewModel
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * TFARequiredView.
 */
@CompileStatic
class TFARequiredView extends AbstractView {
    private boolean debugEnabled

    @Required
    void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled
    }

    @Override
    protected Promise<ViewModel> buildViewModel(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def client = contextWrapper.client
        def userTFA = contextWrapper.userTFA

        def modelMap = [
                'locale': contextWrapper.viewLocale,
                'tfaTypes': contextWrapper.TFATypes
        ]

        if ((debugEnabled || client.debugEnabled) && userTFA != null) {
            modelMap['verifyCode'] = userTFA.verifyCode
        }

        def model = new ViewModel(
                view: 'TFARequiredView',
                model: modelMap as Map<String, Object>,
                errors: contextWrapper.errors.unique(errorComparator).asList()
        )

        return Promise.pure(model)
    }
}
