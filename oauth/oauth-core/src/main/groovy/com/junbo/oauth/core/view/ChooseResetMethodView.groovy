package com.junbo.oauth.core.view

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.spec.model.ViewModel
import groovy.transform.CompileStatic

/**
 * Created by minhao on 5/10/14.
 */
@CompileStatic
class ChooseResetMethodView extends AbstractView {

    @Override
    protected Promise<ViewModel> buildViewModel(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def modelMap = [
                'locale': contextWrapper.viewLocale,
                'methods': ['email', 'security_question', 'sms']
        ]

        def model = new ViewModel(
                view: 'choose_reset_method',
                model: modelMap as Map<String, Object>,
                errors: contextWrapper.errors.unique(errorComparator).asList()
        )

        return Promise.pure(model)
    }
}
