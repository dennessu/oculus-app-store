package com.junbo.order.core.impl.orderaction

import com.junbo.email.spec.model.Email
import com.junbo.identity.spec.model.user.User
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.clientproxy.FacadeContainer
import com.junbo.order.clientproxy.model.OrderOffer
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier

import javax.annotation.Resource

/**
 * Created by LinYi on 14-3-14.
 */
@CompileStatic
class SendEmailAction implements Action {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendEmailAction)

    @Autowired
    @Qualifier('orderFacadeContainer')
    FacadeContainer facadeContainer

    @Resource(name = 'orderServiceContextBuilder')
    OrderServiceContextBuilder orderServiceContextBuilder

    @Override
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        def order = context.orderServiceContext.order
        def catalogOffers = []
        orderServiceContextBuilder.getOffers(context.orderServiceContext).then { List<OrderOffer> ofs ->
            ofs.each { OrderOffer offer -> catalogOffers.add(offer.catalogOffer) }
            return orderServiceContextBuilder.getUser(context.orderServiceContext).then { User u ->
                return facadeContainer.emailFacade.sendOrderConfirmationEMail(
                        order, u, catalogOffers).syncRecover { Throwable ex ->
                    LOGGER.error('name=SendEmail_Action_Fail', ex)
                    return Promise.pure(null)
                }.then { Email email ->
                    LOGGER.info('name=SendEmail_Action_Success, id={}, userId={}', email.id, email.userId)
                    return Promise.pure(null)
                }
            }
        }
    }
}
