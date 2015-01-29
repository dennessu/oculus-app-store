package com.junbo.order.core.impl.orderaction
import com.junbo.catalog.spec.model.item.Item
import com.junbo.catalog.spec.resource.OfferResource
import com.junbo.catalog.spec.resource.OfferRevisionResource
import com.junbo.common.id.ItemId
import com.junbo.common.id.OfferId
import com.junbo.common.util.IdFormatter
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.clientproxy.catalog.CatalogFacade
import com.junbo.order.clientproxy.model.Offer
import com.junbo.order.clientproxy.rating.RatingFacade
import com.junbo.order.core.impl.common.TransactionHelper
import com.junbo.order.core.impl.order.OrderServiceContext
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.core.impl.subledger.SubledgerHelper
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.OrderItem
import com.junbo.order.spec.model.SubledgerItem
import com.junbo.order.spec.model.enums.SubledgerItemStatus
import com.junbo.order.spec.model.enums.SubledgerType
import com.junbo.order.spec.resource.SubledgerItemResource
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
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

    @Resource(name = 'orderRatingFacade')
    RatingFacade ratingFacade

    @Resource(name = 'orderCatalogFacade')
    CatalogFacade catalogFacade

    @Resource(name = 'order.offerClient')
    OfferResource offerResource

    @Resource(name = 'order.offerRevisionClient')
    OfferRevisionResource offerRevisionResource

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
        return builder.getOffers(serviceContext).then {
            Promise.each(serviceContext.order.orderItems) { OrderItem orderItem ->
                getSubledgerItemsFromOffer(serviceContext.offersMap[orderItem.offer], order, orderItem).then { List<SubledgerItem> subledgerItems ->
                    subledgerItems.each { SubledgerItem subledgerItem ->
                        subledgerItemResource.createSubledgerItem(subledgerItem)
                    }
                    LOGGER.error('name=SulbedgerItemCreated, orderId={}, numOfCreated={}', IdFormatter.encodeId(serviceContext.order.getId()), subledgerItems.size())
                    return Promise.pure()
                }
            }
        }.then {
            return Promise.pure()
        }
    }

    private Promise<List<SubledgerItem>> getSubledgerItemsFromOffer(Offer offer, Order order, OrderItem orderItem) {
        // todo :handle subOffers in the future
        List<SubledgerItem> results = []
        Map<ItemId, BigDecimal> itemPrices = [:]

        if (CollectionUtils.isEmpty(offer.items)) {
            LOGGER.error('name=Empty_Offer_Revisions, offerRevisionId={}', offer.revisionId)
            return Promise.pure(results)
        }

        BigDecimal totalPrice = 0
        BigDecimal totalAmount = order.isTaxInclusive ? orderItem.totalAmount - orderItem.totalTax : orderItem.totalAmount
        BigDecimal totalPayoutAmount = orderItem.developerRevenue == null ? BigDecimal.ZERO : orderItem.developerRevenue

        Promise.each(offer.items) { com.junbo.order.clientproxy.model.ItemEntry itemEntry ->
            Item item = itemEntry.item

            SubledgerItem subledgerItem = new SubledgerItem()
            results << subledgerItem
            subledgerItem.item = new ItemId(item.itemId)
            subledgerItem.orderItem = orderItem.getId()
            subledgerItem.subledgerType = SubledgerType.PAYOUT.name()
            subledgerItem.totalQuantity = itemEntry.quantity * orderItem.quantity
            subledgerItem.status = initialStatus.name()

            if (item.defaultOffer == null) {
                subledgerItem.offer = new OfferId(offer.getId())
                itemPrices[new ItemId(item.itemId)] = 0 as BigDecimal
                if (offer.items.size() > 0) {
                    LOGGER.error('name=Null_Default_Offer_In_Bundle, offer={}, item={}', offer.getId(), item.getId())
                }
                return Promise.pure()
            }

            return catalogFacade.getLatestOfferRevision(item.defaultOffer).then { Offer of ->
                subledgerItem.offer = new OfferId(item.defaultOffer)
                results << subledgerItem
                def price = of?.price?.prices[order.country.value][order.currency.value]
                itemPrices[new ItemId(item.itemId)] = (price == null ? BigDecimal.ZERO : price)
                if (price != null) {
                    totalPrice += price
                }
                return Promise.pure()
            }
        }.then {
            if (results.size() == 1) {
                results[0].totalAmount = totalAmount
                results[0].totalPayoutAmount = totalPayoutAmount
                results[0].taxAmount = orderItem.totalTax
                return Promise.pure(results)
            } else {
                for (SubledgerItem si : results) {
                    def price = itemPrices[si.item]
                    si.totalAmount = totalAmount * price / totalPrice
                    si.totalPayoutAmount = totalPayoutAmount * price / totalPrice
                    si.taxAmount = orderItem.totalTax * price / totalPrice
                }
            }
            return Promise.pure(results)
        }
    }

    @Override
    void afterPropertiesSet() throws Exception {
        if (initialStatus == null) {
            initialStatus = SubledgerItemStatus.PENDING_PROCESS
        }
    }
}
