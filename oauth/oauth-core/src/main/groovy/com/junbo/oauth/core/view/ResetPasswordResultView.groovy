package com.junbo.oauth.core.view

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.spec.model.ViewModel

/**
 * Created by minhao on 5/1/14.
 */
class ResetPasswordResultView extends AbstractView {
    @Override
    protected Promise<ViewModel> buildViewModel(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def modelMap = [
                'reset_password_success': contextWrapper.errors.isEmpty() ? 'true' : 'false',
                'locale': contextWrapper.viewLocale
        ]

        def model = new ViewModel(
                view: 'reset_password_result',
                model: modelMap as Map<String, Object>,
                errors: contextWrapper.errors.unique(new ErrorComparator()).asList()
        )

        return Promise.pure(model)
    }
}
