package com.junbo.order.core.impl.orderaction

import com.junbo.identity.spec.model.user.User
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.spec.error.AppErrors
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Created by chriszhu on 3/10/14.
 */
@CompileStatic
@Component('validateUserAction')
class ValidateUserAction implements Action {

    @Autowired
    OrderServiceContextBuilder orderServiceContextBuilder

    @Override
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)

        return orderServiceContextBuilder.getUser(context.orderServiceContext).syncThen { User user ->
            if (user.status != 'ACTIVE') {
                throw AppErrors.INSTANCE.userStatusInvalid().exception()
            }
            return context
        }
    }
}
