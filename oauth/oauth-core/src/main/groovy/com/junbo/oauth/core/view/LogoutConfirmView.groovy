package com.junbo.oauth.core.view

import com.junbo.common.id.UserId
import com.junbo.common.util.IdFormatter
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.spec.model.ViewModel
import groovy.transform.CompileStatic

/**
 * Created by haomin on 9/11/14.
 */
@CompileStatic
class LogoutConfirmView extends AbstractView {

    @Override
    protected Promise<ViewModel> buildViewModel(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def modelMap = [
                'userId'  : IdFormatter.encodeId(new UserId(contextWrapper.loginState?.userId)),
                'locale'  : contextWrapper.viewLocale
        ]

        def model = new ViewModel(
                view: 'logout-confirm',
                model: modelMap as Map<String, Object>,
                errors: contextWrapper.errors.unique(errorComparator).asList()
        )

        return Promise.pure(model)
    }
}
