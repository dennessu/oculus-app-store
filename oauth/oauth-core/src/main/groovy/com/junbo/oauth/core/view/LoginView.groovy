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
import org.springframework.beans.factory.annotation.Required

/**
 * RedirectToPage.
 */
@CompileStatic
class LoginView extends AbstractView {
    private String recaptchaPublicKey

    @Required
    void setRecaptchaPublicKey(String recaptchaPublicKey) {
        this.recaptchaPublicKey = recaptchaPublicKey
    }

    @Override
    protected Promise<ViewModel> buildViewModel(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def modelMap = [
                'clientId': contextWrapper.client.clientId,
                'userId'  : contextWrapper.loginState?.userId,
                'locale'  : contextWrapper.viewLocale
        ]
        modelMap.putAll(contextWrapper.extraParameterMap)

        def model = new ViewModel(
                view: 'login',
                model: modelMap as Map<String, Object>,
                errors: contextWrapper.errors.unique(new ErrorComparator()).asList()
        )

        if (contextWrapper.captchaRequired) {
            model.model['captchaRequired'] = contextWrapper.captchaRequired
            model.model['recaptchaPublicKey'] = recaptchaPublicKey
        }

        return Promise.pure(model)
    }
}
