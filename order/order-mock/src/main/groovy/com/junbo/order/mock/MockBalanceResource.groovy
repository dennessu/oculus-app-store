package com.junbo.order.mock
import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.resource.BalanceResource
import com.junbo.common.id.BalanceId
import com.junbo.langur.core.promise.Promise
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import java.util.concurrent.ConcurrentHashMap

/**
 * Created by chriszhu on 2/20/14.
 */
@Component('mockBalanceResource')
@Scope('prototype')
class MockBalanceResource extends BaseMock implements BalanceResource {

    private final Map<BalanceId, Balance> balanceMap = new ConcurrentHashMap<>()

    @Override
    Promise<Balance> postBalance(Balance balance) {
        balance.balanceId = new BalanceId()
        balance.balanceId.value = generateLong()
        balance.status = 'Open'
        balanceMap[balance.balanceId] = balance
        return Promise.pure(balance)
    }

    @Override
    Promise<Balance> getBalance(Long balanceId) {
        def balance = new Balance()
        balance.balanceId = new BalanceId()
        balance.balanceId.value = balanceId
        balance.status = 'Open'
        return Promise.pure(balance)
    }

    @Override
    Promise<List<Balance>> getBalances(Long orderId) {
        return Promise.pure(balanceMap.values().findAll { Balance balance ->
            balance.orderId.value == orderId
        })
    }

    @Override
    Promise<Balance> quoteBalance(Balance balance) {
        balance.balanceId = new BalanceId()
        balance.balanceId.value = generateLong()
        balance.status = 'Open'
        balance.taxAmount = '2.00'
        balance.taxIncluded = false
        return Promise.pure(balance)
    }
}
