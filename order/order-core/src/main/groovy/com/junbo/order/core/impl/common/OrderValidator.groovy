package com.junbo.order.core.impl.common

import com.junbo.common.id.PaymentInstrumentId
import com.junbo.order.clientproxy.FacadeContainer
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.Order
import com.junbo.payment.spec.enums.PIType
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
            throw AppErrors.INSTANCE.fieldInvalid(fieldName, 'value could not be null').exception()
        }
        return this
    }

    OrderValidator notEmpty(Collection val, String fieldName) {
        if (CollectionUtils.isEmpty(val)) {
            throw AppErrors.INSTANCE.fieldInvalid(fieldName, 'value could not be empty').exception()
        }
        return this
    }

    OrderValidator validEnumString(String enumString, String fieldName, Class enumClass) {
        assert enumClass.isEnum()
        if (enumString != null) {
            try {
                Enum.valueOf(enumClass, enumString)
            } catch (IllegalArgumentException) {
                throw AppErrors.INSTANCE.fieldInvalid(fieldName).exception()
            }
        }
        return this
    }

    OrderValidator validCurrency(String currencyString, String fieldName) {
        def currency = facadeContainer.billingFacade.getCurrency(currencyString).wrapped().get()
        if (currency == null) {
            throw AppErrors.INSTANCE.fieldInvalid(fieldName, 'not a valid currency').exception()
        }
        return this
    }

    OrderValidator validWebPaymentUrls(List<PaymentInstrumentId> piids,
                                       String successRedirectUrl, String cancelRedirectUrl) {
        if (piids == null || piids.isEmpty()) {
            return this
        }
        def pi = facadeContainer.paymentFacade.getPaymentInstrument(piids[0].value).wrapped().get()
        if (pi?.type == PIType.PAYPAL.name()) {
            notNull(successRedirectUrl, 'successRedirectUrl')
            notNull(cancelRedirectUrl, 'cancelRedirectUrl')
        }
        return this
    }

    OrderValidator between(Number val, Number min, Number max, String fieldName) {
        if (val != null) {
            if ((min != null && val < min) || (max != null && val > max)) {
                throw AppErrors.INSTANCE.fieldInvalid(fieldName).exception()
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
                    notNull(order.shippingMethod, 'shippingMethodId')
                    notNull(order.shippingAddress, 'shippingAddressId')
                }
                if (!CoreUtils.isFreeOrder(order)) {
                    notEmpty(order.paymentInstruments, 'paymentInstruments')
                }
            }
        }
    }
}