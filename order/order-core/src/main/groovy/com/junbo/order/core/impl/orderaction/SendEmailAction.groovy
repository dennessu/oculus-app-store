package com.junbo.order.core.impl.orderaction

import com.junbo.catalog.spec.model.offer.Offer
import com.junbo.identity.spec.model.user.User
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.clientproxy.catalog.CatalogFacade
import com.junbo.order.clientproxy.email.EmailFacade
import com.junbo.order.clientproxy.identity.IdentityFacade
import com.junbo.order.spec.model.OrderItem
import groovy.transform.CompileStatic

import javax.annotation.Resource

/**
 * Created by LinYi on 14-3-14.
 */
@CompileStatic
class SendEmailAction implements Action {
    @Resource
    EmailFacade emailFacade

    @Resource
    IdentityFacade identityFacade

    @Resource
    CatalogFacade catalogFacade

    @Override
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        def order = context.orderServiceContext.order
        def offerIds = []
        order.orderItems.each { OrderItem item ->
            offerIds << item.offer
        }

        return catalogFacade.getOffers(offerIds).then { List<Offer> offers ->
             identityFacade.getUser(order.user.value).then { User user ->
                emailFacade.sendOrderConfirmationEMail(order, user, offers)
             }
        }
    }
}
