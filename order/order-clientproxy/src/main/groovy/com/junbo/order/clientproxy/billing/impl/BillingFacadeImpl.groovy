package com.junbo.order.clientproxy.billing.impl

import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.ShippingAddress
import com.junbo.billing.spec.resource.BalanceResource
import com.junbo.billing.spec.resource.BillingCurrencyResource
import com.junbo.billing.spec.resource.ShippingAddressResource
import com.junbo.common.id.BalanceId
import com.junbo.common.id.OrderId
import com.junbo.common.id.ShippingAddressId
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
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
@TypeChecked
@Component('orderBillingFacade')
class BillingFacadeImpl implements BillingFacade {
    @Resource(name='order.billingBalanceClient')
    BalanceResource balanceResource
    @Resource(name='order.billingShippingAddressClient')
    ShippingAddressResource shippingAddressResource
    @Resource(name='order.billingCurrencyClient')
    BillingCurrencyResource billingCurrencyResource

    private Map<String, com.junbo.billing.spec.model.Currency> currencyMap

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
        return balanceResource.getBalances(new OrderId(orderId)).syncThen { Results<Balance> results ->
            return results == null ? Collections.emptyList() : results.items
        }
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

    @Override
    Promise<List<com.junbo.billing.spec.model.Currency>> getCurrencies() {
        if (currencyMap != null) {
            return Promise.pure(new ArrayList<com.junbo.billing.spec.model.Currency>(currencyMap.values()))
        }
        billingCurrencyResource.currencies.syncThen { Results<Currency> results ->
            def val = new HashMap<>()
            results?.items?.each { com.junbo.billing.spec.model.Currency currency ->
                val[currency.name] = currency
            }
            currencyMap = val
        }
    }

    @Override
    Promise<com.junbo.billing.spec.model.Currency> getCurrency(String name) {
        currencies.syncThen {
            return currencyMap.get(name)
        }
    }

    @Override
    Promise<Balance> confirmBalance(Balance balance) {
        return balanceResource.confirmBalance(balance)
    }
}
