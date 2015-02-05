package com.junbo.store.clientproxy.order
import com.junbo.common.enumid.CurrencyId
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.OfferId
import com.junbo.common.id.OrderId
import com.junbo.common.id.PaymentInstrumentId
import com.junbo.common.id.UserId
import com.junbo.langur.core.context.JunboHttpContext
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
    Promise<Order> createTentativeOrder(List<OfferId> offerIdList, CurrencyId currencyId, PaymentInstrumentId paymentInstrumentId, ApiContext apiContext) {
        Order order = new Order(
                user: apiContext.user,
                country: apiContext.country.getId(),
                currency: currencyId,
                locale: apiContext.locale.getId(),
                tentative: true,
                orderItems: offerIdList.collect {OfferId offerId -> new OrderItem(offer: offerId, quantity: 1)},
                payments: paymentInstrumentId == null ? null : [new PaymentInfo(paymentInstrument : paymentInstrumentId)] as List
        )
        return resourceContainer.orderResource.createOrder(order)
    }

    @Override
    Promise<Order> updateTentativeOrder(OrderId orderId, List<OfferId> offerIdList, CurrencyId currencyId, PaymentInstrumentId paymentInstrumentId, ApiContext apiContext) {
        return resourceContainer.orderResource.getOrderByOrderId(orderId).then { Order order ->
            if (order.user != apiContext.user) {
                throw AppCommonErrors.INSTANCE.forbiddenWithMessage('Order not owned by the User.').exception()
            }
            order.orderItems = offerIdList.collect {OfferId offerId -> new OrderItem(offer: offerId, quantity: 1)}
            order.payments = paymentInstrumentId == null ? null : [new PaymentInfo(paymentInstrument : paymentInstrumentId)] as List
            order.country = apiContext.country.getId()
            order.currency = currencyId
            order.locale = apiContext.locale.getId()
            return resourceContainer.orderResource.updateOrderByOrderId(orderId, order)
        }
    }

    @Override
    Promise<Order> settleOrder(OrderId orderId, ApiContext apiContext) {
        setHeaderValue('X-CLIENT-NAME', apiContext.clientName)
        setHeaderValue('X-CLIENT-VERSION', apiContext.clientVersion)
        setHeaderValue('X-PLATFORM-NAME', apiContext.platform.value)
        setHeaderValue('X-PLATFORM-VERSION', apiContext.platformVersion)
        resourceContainer.orderResource.getOrderByOrderId(orderId).then { Order order ->
            order.tentative = false
            return resourceContainer.orderResource.updateOrderByOrderId(order.getId(), order)
        }
    }

    private void setHeaderValue(String name, String val) {
        if (val != null) {
            JunboHttpContext.requestHeaders.putSingle(name, val)
        }
    }

    private Promise<Order> createAndSettle(UserId userId, List<OfferId> offerIdList, CurrencyId currencyId, ApiContext apiContext) {
        Order order = new Order(
                user: userId,
                country: apiContext.country.getId(),
                currency: currencyId,
                locale: apiContext.locale.getId(),
                tentative: false,
                orderItems: offerIdList.collect {OfferId offerId -> new OrderItem(offer: offerId, quantity: 1)}
        )

        return resourceContainer.orderResource.createOrder(order).then { Order settled ->
            return Promise.pure(settled)
        }
    }
}
