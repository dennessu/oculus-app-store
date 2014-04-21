package com.junbo.order.core.impl.subledger

import com.junbo.common.id.OfferId
import com.junbo.common.id.UserId
import com.junbo.order.clientproxy.catalog.CatalogFacade
import com.junbo.order.clientproxy.model.OrderOfferRevision
import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.SubledgerItem
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

/**
 * Created by fzhang on 4/10/2014.
 */
@CompileStatic
@Component('subledgerItemContextBuilder')
class SubledgerItemContextBuilder {

    OrderRepository orderRepository

    CatalogFacade catalogFacade

    SubledgerItemContext buildContext(OrderOfferRevision offer, String country, String currency, Date createdTime) {
        return new SubledgerItemContext(
            sellerId : new UserId(offer.catalogOfferRevision.ownerId),
            offerId : new OfferId(offer.catalogOfferRevision.offerId),
            currency : currency,
            country : country,
            createdTime : createdTime
        )
    }

    SubledgerItemContext buildContext(SubledgerItem subledgerItem) {
        def orderItem = orderRepository.getOrderItem(subledgerItem.orderItemId.value)
        if (orderItem == null) {
            throw AppErrors.INSTANCE.orderItemNotFound().exception()
        }

        def order = orderRepository.getOrder(orderItem.orderId.value)
        if (order == null) {
            throw AppErrors.INSTANCE.orderNotFound().exception()
        }

        def offer = catalogFacade.getOfferRevision(subledgerItem.offerRevisionId.value).wrapped().get()
        return buildContext(offer, order.country, order.currency, subledgerItem.createdTime)
    }
}
