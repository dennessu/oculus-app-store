package com.junbo.oauth.core.view

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.spec.model.ViewModel
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * EmailVerifyRequiredView.
 */
@CompileStatic
class EmailVerifyRequiredView extends AbstractView {
    private boolean debugEnabled

    @Required
    void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled
    }

    @Override
    protected Promise<ViewModel> buildViewModel(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def client = contextWrapper.client
        assert client != null : 'client is null'

        def modelMap = [
                'username': contextWrapper.user.username,
                'email': contextWrapper.userDefaultEmail,
                'locale': contextWrapper.viewLocale,
                'REGISTRATIONSTATE': contextWrapper.registrationState
        ]

        if (debugEnabled || client.debugEnabled) {
            modelMap['link'] = contextWrapper.emailVerifyLink
        }

        def model = new ViewModel(
                view: 'emailVerifyRequired',
                model: modelMap as Map<String, Object>,
                errors: contextWrapper.errors.unique(errorComparator).asList()
        )

        return Promise.pure(model)
    }
}
