package com.junbo.oauth.core.view

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.spec.model.ViewModel
import org.springframework.beans.factory.annotation.Required

/**
 * Created by Zhanxin on 5/21/2014.
 */
class PaymentMethodView extends AbstractView {
    private String btCSE

    @Required
    void setBtCSE(String btCSE) {
        this.btCSE = btCSE
    }

    @Override
    protected Promise<ViewModel> buildViewModel(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def modelMap = [
                'clientId'          : contextWrapper.client.clientId,
                'CES'               : btCSE,
                'locale'            : contextWrapper.viewLocale
        ]
        modelMap.putAll(contextWrapper.extraParameterMap)

        def model = new ViewModel(
                view: 'payment-method',
                model: modelMap as Map<String, Object>,
                errors: contextWrapper.errors.unique(errorComparator).asList()
        )

        return Promise.pure(model)
    }
}
