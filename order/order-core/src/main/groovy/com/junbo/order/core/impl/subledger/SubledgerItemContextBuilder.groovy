package com.junbo.order.core.impl.subledger

import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.CurrencyId
import com.junbo.common.id.OfferId
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.catalog.CatalogFacade
import com.junbo.order.clientproxy.model.OrderOfferRevision
import com.junbo.order.db.repo.facade.OrderRepositoryFacade
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.SubledgerItem
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 * Created by fzhang on 4/10/2014.
 */
@CompileStatic
@Component('subledgerItemContextBuilder')
class SubledgerItemContextBuilder {

    @Resource(name = 'orderRepositoryFacade')
    OrderRepositoryFacade orderRepository

    @Resource(name = 'cachedCatalogFacade')
    CatalogFacade catalogFacade

    SubledgerItemContext buildContext(OrderOfferRevision offer, CountryId country, CurrencyId currency, Date createdTime) {
        return new SubledgerItemContext(
            seller : offer.catalogOfferRevision.ownerId,
            offer : new OfferId(offer.catalogOfferRevision.offerId),
            currency : currency,
            country : country,
            createdTime : createdTime
        )
    }

    SubledgerItemContext buildContext(SubledgerItem subledgerItem) {
        def orderItem = orderRepository.getOrderItem(subledgerItem.orderItem.value)
        if (orderItem == null) {
            throw AppErrors.INSTANCE.orderItemNotFound().exception()
        }

        def order = orderRepository.getOrder(orderItem.orderId.value)
        if (order == null) {
            throw AppErrors.INSTANCE.orderNotFound().exception()
        }

        def offer = Promise.get { catalogFacade.getOfferRevision(subledgerItem.offer.value, orderItem.honoredTime) }
        return buildContext(offer, order.country, order.currency, subledgerItem.createdTime)
    }
}
