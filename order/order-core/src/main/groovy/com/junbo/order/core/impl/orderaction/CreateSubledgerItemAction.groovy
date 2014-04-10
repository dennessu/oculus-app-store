package com.junbo.order.core.impl.orderaction

import com.junbo.common.id.OfferId
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.clientproxy.model.OrderOffer
import com.junbo.order.core.SubledgerService
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.core.impl.subledger.SubledgerHelper
import com.junbo.order.db.entity.enums.SubledgerItemAction
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.OrderItem
import com.junbo.order.spec.model.SubledgerItem

import javax.annotation.Resource
/**
 * Created by fzhang on 4/10/2014.
 */
class CreateSubledgerItemAction extends BaseOrderEventAwareAction {

    @Resource(name = 'orderSubledgerHelper')
    SubledgerHelper subledgerHelper

    @Resource(name = 'orderSubledgerService')
    SubledgerService subledgerService

    @Resource(name = 'orderServiceContextBuilder')
    OrderServiceContextBuilder builder

    @Override
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        def serviceContext = context.orderServiceContext
        def order = serviceContext.order

        builder.getOffers(serviceContext).syncThen {
            serviceContext.order.orderItems?.each { OrderItem orderItem ->
                def offer = serviceContext.offers[orderItem.offer]
                def subledgerItem = buildSubledgerItem(order, orderItem, offer)
                def subledger = subledgerHelper.getMatchingSubledger(offer, order.country, order.currency, new Date())
                if (subledger != null) {
                    // link to subledger only if matching subledger found. If not found, let the back-end job to create
                    // the subledger so as to avoid concurrent creation of same subledger
                    subledgerItem.subledgerId = subledger.subledgerId
                }
                subledgerService.createSubledgerItem(subledgerItem)
            }
        }
        return null
    }

    private SubledgerItem buildSubledgerItem(Order order, OrderItem orderItem, OrderOffer offer) {
        // todo handle refund subledger item logic
        def subledgerItem = new SubledgerItem(
                totalAmount: order.isTaxInclusive ? orderItem.totalAmount - orderItem.totalTax : orderItem.totalAmount,
                orderItemId: orderItem.orderItemId,
                offerId: new OfferId(offer.catalogOffer.id),
                subledgerItemAction: SubledgerItemAction.CHARGE
        )
        return subledgerItem
    }
}
