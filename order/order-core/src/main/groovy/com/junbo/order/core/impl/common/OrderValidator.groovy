package com.junbo.order.core.impl.common

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.PIType
import com.junbo.common.id.PaymentInstrumentId
import com.junbo.order.clientproxy.FacadeContainer
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.OrderItem
import com.junbo.payment.spec.model.PaymentInstrument
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.apache.commons.collections.CollectionUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

/**
 * Class to do some general validation on order
 * Created by chriszhu on 3/18/14.
 */
@CompileStatic
@TypeChecked
@Component('orderValidator')
class OrderValidator {

    @Autowired
    @Qualifier('orderFacadeContainer')
    FacadeContainer facadeContainer

    OrderValidator notNull(Object val, String fieldName) {
        if (val == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired(fieldName).exception()
        }
        return this
    }

    OrderValidator notEmpty(Collection val, String fieldName) {
        if (CollectionUtils.isEmpty(val)) {
            throw AppCommonErrors.INSTANCE.fieldRequired(fieldName).exception()
        }
        return this
    }

    OrderValidator validEnumString(String enumString, String fieldName, Class enumClass) {
        assert enumClass.isEnum()
        if (enumString != null) {
            try {
                Enum.valueOf(enumClass, enumString)
            } catch (IllegalArgumentException) {
                throw AppCommonErrors.INSTANCE.fieldInvalid(fieldName).exception()
            }
        }
        return this
    }

    OrderValidator validWebPaymentUrls(List<PaymentInstrumentId> piids,
                                       String successRedirectUrl, String cancelRedirectUrl) {
        if (piids == null || piids.isEmpty()) {
            return this
        }
        def pi = facadeContainer.paymentFacade.getPaymentInstrument(piids[0].value).get()
        if (PIType.get(pi?.type) == PIType.PAYPAL || PIType.get(pi?.type) == PIType.OTHERS) {
            notNull(successRedirectUrl, 'successRedirectUrl')
            notNull(cancelRedirectUrl, 'cancelRedirectUrl')
        }
        return this
    }

    OrderValidator between(Number val, Number min, Number max, String fieldName) {
        if (val != null) {
            if ((min != null && val < min) || (max != null && val > max)) {
                throw AppCommonErrors.INSTANCE.fieldInvalid(fieldName).exception()
            }
        }
        return this
    }

    OrderValidator validateSettleOrderRequest(Order order) {
        assert (order != null)
        if (!order.tentative) {
            // validate pi is there if amount is zero
            if (!order.tentative) {
                if (CoreUtils.hasPhysicalOffer(order)) {
                    notNull(order.shippingMethod, 'shippingMethod')
                    notNull(order.shippingAddress, 'shippingToAddress')
                    notNull(order.shippingToName, 'shippingToName')
                    notNull(order.shippingToPhone, 'shippingToPhone')
                }
                if (!CoreUtils.isFreeOrder(order)) {
                    notEmpty(order.payments, 'payments')
                }
            }
        }
    }

    OrderValidator validateRefundOrderRequest(Order order) {
        assert (order != null)
        if (order.tentative) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('tentative').exception()
        }
        order.orderItems?.each { OrderItem item ->
            if (item.quantity < 0) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('orderItem.quantity').exception()
            }
            if (item.totalAmount < BigDecimal.ZERO) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('orderItem.totalAmount').exception()
            }
        }
        return this
    }

    OrderValidator validatePaymentInstrument(PaymentInstrument pi, Order order) {
        if (pi.userId != null && order.user.value != pi.userId) {
            throw AppCommonErrors.INSTANCE.fieldInvalid(
                    'payments', 'do not belong to this user').exception()
        }
        if (PIType.get(pi.type) == PIType.CREDITCARD) {
            String expDate = pi.typeSpecificDetails.expireDate
            assert (expDate != null)
            Date expireDate = CoreBuilder.buildDate(expDate)
            Date now = new Date()
            if (expireDate.before(now)) {
                throw AppCommonErrors.INSTANCE.fieldInvalid(
                        'payments', 'PI expired').exception()
            }
        }
    }
}