package com.junbo.order.core.impl.orderaction

import com.junbo.catalog.spec.model.offer.Offer
import com.junbo.email.spec.model.Email
import com.junbo.identity.spec.model.user.User
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.clientproxy.FacadeContainer
import com.junbo.order.spec.model.OrderItem
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier

/**
 * Created by LinYi on 14-3-14.
 */
@CompileStatic
class SendEmailAction implements Action {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendEmailAction)

    @Autowired
    @Qualifier('orderFacadeContainer')
    FacadeContainer facadeContainer

    @Override
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        def order = context.orderServiceContext.order
        def offerIds = []
        order.orderItems.each { OrderItem item ->
            offerIds << item.offer
        }

        return facadeContainer.catalogFacade.getOffers(offerIds).then { List<Offer> offers ->
            facadeContainer.identityFacade.getUser(order.user.value).then { User user ->
                facadeContainer.emailFacade.sendOrderConfirmationEMail(order, user, offers)
             }
        }.syncRecover { Throwable ex ->
            LOGGER.error('name=SendEmail_Action_Fail', ex)
            return null
        }.syncThen {  Email email ->
            LOGGER.info('name=SendEmail_Action_Success, id={}, userId={}', email.id, email.userId)
        }
    }
}
