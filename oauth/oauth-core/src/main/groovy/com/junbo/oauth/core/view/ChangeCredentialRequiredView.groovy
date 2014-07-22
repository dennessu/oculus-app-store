package com.junbo.oauth.core.view

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.spec.model.ViewModel
import groovy.transform.CompileStatic

/**
 * Created by haomin on 14-6-13.
 */
@CompileStatic
class ChangeCredentialRequiredView extends AbstractView {
    @Override
    protected Promise<ViewModel> buildViewModel(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def modelMap = [
                'username': contextWrapper.user.username,
                'email': contextWrapper.userDefaultEmail,
                'locale': contextWrapper.viewLocale
        ]

        def model = new ViewModel(
                view: 'changeCredentialRequired',
                model: modelMap as Map<String, Object>,
                errors: contextWrapper.errors.unique(errorComparator).asList()
        )

        return Promise.pure(model)
    }
}
