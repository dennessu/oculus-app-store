package com.junbo.store.clientproxy.order

import com.junbo.common.enumid.CurrencyId
import com.junbo.common.id.OfferId
import com.junbo.common.id.PaymentInstrumentId
import com.junbo.common.id.UserId
import com.junbo.langur.core.promise.Promise
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.OrderItem
import com.junbo.order.spec.model.PaymentInfo
import com.junbo.store.clientproxy.ResourceContainer
import com.junbo.store.spec.model.ApiContext
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 * The OrderFacadeImpl class.
 */
@CompileStatic
@Component('storeOrderFacade')
class OrderFacadeImpl implements OrderFacade {

    public static final String FREE_PURCHASE_CURRENCY = "XXX"

    @Resource(name = 'storeResourceContainer')
    private ResourceContainer resourceContainer

    @Override
    Promise<Order> freePurchaseOrder(UserId userId, List<OfferId> offerIdList, ApiContext apiContext) {
        return createAndSettle(userId, offerIdList, new CurrencyId(FREE_PURCHASE_CURRENCY), apiContext)
    }

    @Override
    Promise<Order> createTentativeOrder(UserId userId, List<OfferId> offerIdList, CurrencyId currencyId, PaymentInstrumentId paymentInstrumentId, ApiContext apiContext) {
        Order order = new Order(
                user: userId,
                country: apiContext.country.getId(),
                currency: currencyId,
                locale: apiContext.locale.getId(),
                tentative: true,
                orderItems: offerIdList.collect {OfferId offerId -> new OrderItem(offer: offerId, quantity: 1)},
                payments: paymentInstrumentId == null ? null : [new PaymentInfo(paymentInstrument : paymentInstrumentId)] as List
        )
        return resourceContainer.orderResource.createOrder(order)
    }

    private Promise<Order> createAndSettle(UserId userId, List<OfferId> offerIdList, CurrencyId currencyId, ApiContext apiContext) {
        Order order = new Order(
                user: userId,
                country: apiContext.country.getId(),
                currency: currencyId,
                locale: apiContext.locale.getId(),
                tentative: true,
                orderItems: offerIdList.collect {OfferId offerId -> new OrderItem(offer: offerId, quantity: 1)}
        )

        return resourceContainer.orderResource.createOrder(order).then { Order o ->
            order = o
            return Promise.pure()
        }.then {
            order.tentative = false
            resourceContainer.orderResource.updateOrderByOrderId(order.getId(), order).then { Order settled ->
                return Promise.pure(settled)
            }
        }
    }
}
