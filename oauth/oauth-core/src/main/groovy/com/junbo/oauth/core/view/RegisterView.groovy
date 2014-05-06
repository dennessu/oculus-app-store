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
 * RegisterView.
 */
@CompileStatic
class RegisterView extends AbstractView {
    private String recaptchaPublicKey

    @Required
    void setRecaptchaPublicKey(String recaptchaPublicKey) {
        this.recaptchaPublicKey = recaptchaPublicKey
    }

    @Override
    protected Promise<ViewModel> buildViewModel(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def modelMap = [
                'clientId'          : contextWrapper.client.clientId,
                'captchaRequired'   : true,
                'recaptchaPublicKey': recaptchaPublicKey,
                'locale'            : contextWrapper.viewLocale
        ]
        modelMap.putAll(contextWrapper.extraParameterMap)

        def model = new ViewModel(
                view: 'register',
                model: modelMap as Map<String, Object>,
                errors: contextWrapper.errors.unique(errorComparator).asList()
        )

        return Promise.pure(model)
    }
}
