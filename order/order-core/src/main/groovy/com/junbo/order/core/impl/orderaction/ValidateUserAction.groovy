package com.junbo.order.core.impl.orderaction
import com.junbo.identity.spec.v1.model.User
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.spec.error.AppErrors
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import javax.annotation.Resource
/**
 * Created by chriszhu on 3/10/14.
 */
@CompileStatic
@TypeChecked
@Component('validateUserAction')
class ValidateUserAction implements Action {

    public static final String USERACTIVE = 'ACTIVE'

    @Resource(name = 'orderServiceContextBuilder')
    OrderServiceContextBuilder orderServiceContextBuilder

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidateUserAction)

    @Override
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)

        return orderServiceContextBuilder.getUser(context.orderServiceContext).syncRecover {
            LOGGER.error('name=Validate_User_Fail')
            throw AppErrors.INSTANCE.userConnectionError().exception()
        }.syncThen { User user ->
            if (user == null) {
                LOGGER.info('name=Validate_User_Not_Found')
                throw AppErrors.INSTANCE.userNotFound(
                        context?.orderServiceContext?.order?.user?.value?.toString()).exception()
            }
            if (!user.status == USERACTIVE) {
                LOGGER.info('name=Validate_User_Invalid')
                throw AppErrors.INSTANCE.userStatusInvalid().exception()
            }
            return null
        }
    }
}
