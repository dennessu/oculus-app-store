package com.junbo.order.clientproxy.billing.impl

import com.junbo.billing.spec.error.ErrorCode
import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.resource.BalanceResource
import com.junbo.billing.spec.resource.BillingCurrencyResource
import com.junbo.common.error.AppError
import com.junbo.common.id.BalanceId
import com.junbo.common.id.OrderId
import com.junbo.common.model.Results
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.billing.BillingFacade
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.error.ErrorUtils
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
    @Resource(name='order.billingCurrencyClient')
    BillingCurrencyResource billingCurrencyResource

    protected Map<String, com.junbo.billing.spec.model.Currency> currencyMap

    @Override
    Promise<Balance> createBalance(Balance balance, Boolean isAsyncCharge) {
        balance.isAsyncCharge = isAsyncCharge
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
    Promise<Balance> quoteBalance(Balance balance) {
        return balanceResource.quoteBalance(balance)
    }

    @Override
    Promise<Collection<com.junbo.billing.spec.model.Currency>> getCurrencies() {
        if (currencyMap != null) {
            return Promise.pure(new ArrayList<com.junbo.billing.spec.model.Currency>(currencyMap.values()))
        }

        return billingCurrencyResource.currencies.syncThen { Results<Currency> results ->
            def val = new HashMap<>()
            results?.items?.each { com.junbo.billing.spec.model.Currency currency ->
                val[currency.name] = currency
            }
            currencyMap = val
            return val.values()
        }
    }

    @Override
    Promise<com.junbo.billing.spec.model.Currency> getCurrency(String name) {
        return currencies.syncThen {
            return currencyMap.get(name)
        }
    }

    @Override
    Promise<Balance> confirmBalance(Balance balance) {
        return balanceResource.confirmBalance(balance)
    }

    @Override
    AppError convertError(Throwable error) {
        AppError e = ErrorUtils.toAppError(error)

        if (e != null && e.code == ErrorCode.PAYMENT_INSUFFICIENT_FUND) {
            return AppErrors.INSTANCE.billingInsufficientFund()
        }

        return AppErrors.INSTANCE.
                billingConnectionError(ErrorUtils.toAppErrors(error))
    }
}
