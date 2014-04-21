package com.junbo.order.core.impl.orderaction

import com.junbo.common.id.OfferRevisionId
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.clientproxy.model.OrderOfferRevision
import com.junbo.order.core.SubledgerService
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.core.impl.subledger.SubledgerHelper
import com.junbo.order.db.entity.enums.SubledgerItemAction
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.OrderItem
import com.junbo.order.spec.model.SubledgerItem
import org.springframework.transaction.annotation.Transactional

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
    @Transactional
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        def serviceContext = context.orderServiceContext
        def order = serviceContext.order

        builder.getOffers(serviceContext).syncThen {
            serviceContext.order.orderItems?.eachWithIndex
                    { OrderItem orderItem, int index ->
                // todo ignore first party item
                def offer = serviceContext.offersMap[orderItem.offer]
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

    private SubledgerItem buildSubledgerItem(Order order, OrderItem orderItem, OrderOfferRevision offer) {
        // todo handle refund subledger item logic
        def subledgerItem = new SubledgerItem(
                totalAmount: order.isTaxInclusive ? orderItem.totalAmount - orderItem.totalTax : orderItem.totalAmount,
                orderItemId: orderItem.orderItemId,
                offerRevisionId: new OfferRevisionId(offer.catalogOfferRevision.revisionId),
                subledgerItemAction: SubledgerItemAction.CHARGE
        )
        return subledgerItem
    }

}
