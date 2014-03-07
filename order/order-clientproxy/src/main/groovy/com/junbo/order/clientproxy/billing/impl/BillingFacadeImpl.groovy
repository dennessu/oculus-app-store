package com.junbo.order.clientproxy.billing.impl

import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.ShippingAddress
import com.junbo.billing.spec.resource.BalanceResource
import com.junbo.billing.spec.resource.ShippingAddressResource
import com.junbo.common.id.BalanceId
import com.junbo.common.id.ShippingAddressId
import com.junbo.common.id.UserId
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.billing.BillingFacade
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 * Created by chriszhu on 2/19/14.
 */
@CompileStatic
@Component('billingFacade')
@TypeChecked
class BillingFacadeImpl implements BillingFacade {
    @Resource(name='billingBalanceClient')
    BalanceResource balanceResource
    @Resource(name='billingShippingAddressClient')
    ShippingAddressResource shippingAddressResource

    @Override
    Promise<Balance> createBalance(Balance balance) {
        return balanceResource.postBalance(balance)
    }

    @Override
    Promise<Balance> settleBalance(Long balanceId) {
        return null
    }

    @Override
    Promise<Balance> captureBalance(Long balanceId) {
        return null
    }

    @Override
    Promise<Balance> getBalanceById(Long balanceId) {
        return balanceResource.getBalance(new BalanceId(balanceId))
    }

    @Override
    Promise<List<Balance>> getBalancesByOrderId(Long orderId) {
        return null
    }

    @Override
    Promise<ShippingAddress> getShippingAddress(Long userId, Long shippingAddressId) {
        return shippingAddressResource.getShippingAddress(
                new UserId(userId), new ShippingAddressId(shippingAddressId)) // provide the user id
    }

    @Override
    Promise<Balance> quoteBalance(Balance balance) {
        return balanceResource.quoteBalance(balance)
    }
}
