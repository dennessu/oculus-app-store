package com.junbo.order.core.impl.orderaction
import com.junbo.common.id.OfferId
import com.junbo.common.util.IdFormatter
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.clientproxy.model.OrderOfferRevision
import com.junbo.order.core.impl.common.TransactionHelper
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.core.impl.subledger.SubledgerHelper
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.OrderItem
import com.junbo.order.spec.model.SubledgerItem
import com.junbo.order.spec.model.enums.SubledgerItemAction
import com.junbo.order.spec.model.enums.SubledgerItemStatus
import com.junbo.order.spec.resource.SubledgerItemResource
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.transaction.annotation.Transactional

import javax.annotation.Resource
/**
 * Created by fzhang on 4/10/2014.
 */
@CompileStatic
class SubledgerCreateItemAction implements Action, InitializingBean {

    private final static Logger LOGGER = LoggerFactory.getLogger(SubledgerCreateItemAction)

    @Resource(name = 'orderSubledgerHelper')
    SubledgerHelper subledgerHelper

    @Resource(name = 'order.subledgerItemClient')
    SubledgerItemResource subledgerItemResource

    @Resource(name = 'orderServiceContextBuilder')
    OrderServiceContextBuilder builder

    @Resource(name = 'orderTransactionHelper')
    TransactionHelper transactionHelper

    SubledgerItemStatus initialStatus

    @Override
    @Transactional
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        def serviceContext = context.orderServiceContext
        transactionHelper.executeInNewTransaction {
            innerExecute(serviceContext)
        }.syncRecover { Throwable ex ->
            LOGGER.error('name=SubledgerCreateItemError, orderId={}', IdFormatter.encodeId(serviceContext.order.getId()), ex)
        }.syncThen {
            return null
        }
    }

    private Promise innerExecute(OrderServiceContext serviceContext) {
        def order = serviceContext.order
        return builder.getOffers(serviceContext).syncThen {
            Promise.each(serviceContext.order.orderItems) { OrderItem orderItem ->
                if (orderItem.developerRevenue == null || orderItem.developerRevenue == BigDecimal.ZERO) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug('skip orderItem for null/0 developerRevenue, orderItemId={} ', orderItem.id)
                    }
                    return Promise.pure(null)
                }

                def offer = serviceContext.offersMap[orderItem.offer]
                def subledgerItem = buildSubledgerItem(order, orderItem, offer)
                subledgerItem.status = initialStatus.name()
                def subledger = subledgerHelper.getMatchingSubledger(offer, order.country, order.currency, new Date())

                if (subledger != null) {
                    // link to subledger only if matching subledger found. If not found, let the back-end job to create
                    // the subledger so as to avoid concurrent creation of same subledger
                    LOGGER.debug('name=Subledger_For_SubledgerItem_Found, orderItemId={}', orderItem.id)
                    subledgerItem.subledger = subledger.getId()
                } else {
                    LOGGER.debug('name=Subledger_For_SubledgerItem_Not_Found, orderItemId={}', orderItem.id)
                }

                subledgerItemResource.createSubledgerItem(subledgerItem)
            }
        }.syncThen {
            return null
        }
    }

    private SubledgerItem buildSubledgerItem(Order order, OrderItem orderItem, OrderOfferRevision offer) {
        def subledgerItem = new SubledgerItem(
                subledgerItemAction: SubledgerItemAction.PAYOUT.name(),
                totalAmount: order.isTaxInclusive ? orderItem.totalAmount - orderItem.totalTax : orderItem.totalAmount,
                totalPayoutAmount: orderItem.developerRevenue == null ? BigDecimal.ZERO : orderItem.developerRevenue,
                totalQuantity: orderItem.quantity.longValue(),
                orderItem: orderItem.getId(),
                offer: new OfferId(offer.catalogOfferRevision.offerId)
        )
        return subledgerItem
    }

    @Override
    void afterPropertiesSet() throws Exception {
        if (initialStatus == null) {
            initialStatus = SubledgerItemStatus.PENDING_PROCESS
        }
    }
}
