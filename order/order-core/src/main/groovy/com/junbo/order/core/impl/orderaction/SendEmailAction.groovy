package com.junbo.order.core.impl.orderaction

import com.junbo.email.spec.model.Email
import com.junbo.identity.spec.v1.model.User
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.clientproxy.FacadeContainer
import com.junbo.order.clientproxy.model.OrderOfferRevision
import com.junbo.order.core.impl.common.CoreUtils
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.apache.commons.collections.CollectionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier

import javax.annotation.Resource

/**
 * Created by LinYi on 14-3-14.
 */
@CompileStatic
@TypeChecked
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
        return orderServiceContextBuilder.getOffers(context.orderServiceContext).recover { Throwable ex ->
            LOGGER.error('name=SendEmail_Action_Fail_On_Fetch_Offer', ex)
            return Promise.pure(null)
        }.then { List<OrderOfferRevision> ofs ->
            if (!CollectionUtils.isEmpty(ofs)) {
                ofs.each { OrderOfferRevision offer -> catalogOffers.add(offer.catalogOfferRevision)
                }
            }
            return orderServiceContextBuilder.getUser(context.orderServiceContext).recover { Throwable ex ->
                LOGGER.error('name=SendEmail_Action_Fail_On_Fetch_User', ex)
                return Promise.pure(null)
            }.then { User u ->
                if (CoreUtils.hasPhysicalOffer(order)) {
                    // TODO: send partial charge email
                    return Promise.pure(null)
                }
                return facadeContainer.emailFacade.sendOrderConfirmationEMail(
                        order, u, catalogOffers).recover { Throwable ex ->
                    LOGGER.error('name=SendEmail_Action_Fail', ex)
                    return Promise.pure(null)
                }.then { Email email ->
                    if (email == null) {
                        LOGGER.error('name=SendEmail_Action_Email_Null')
                        return Promise.pure(null)
                    }
                    LOGGER.info('name=SendEmail_Action_Success, id={}, userId={}', email.id, email.userId)
                    return Promise.pure(null)
                }
            }
        }
    }
}
