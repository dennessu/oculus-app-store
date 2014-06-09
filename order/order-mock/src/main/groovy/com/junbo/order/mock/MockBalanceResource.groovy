package com.junbo.order.mock
import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.BalanceItem
import com.junbo.billing.spec.resource.BalanceResource
import com.junbo.common.id.BalanceId
import com.junbo.common.id.OrderId
import com.junbo.common.model.Results
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import java.util.concurrent.ConcurrentHashMap
/**
 * Created by chriszhu on 2/20/14.
 */
@CompileStatic
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
    Promise<Balance> quoteBalance(Balance balance) {
        balance.balanceId = new BalanceId()
        balance.balanceId.value = generateLong()
        balance.status = 'Open'
        balance.taxAmount = 2.00G
        balance.taxIncluded = false
        balance.balanceItems.each { BalanceItem bi ->
            bi.taxAmount = 1.00G
        }
        return Promise.pure(balance)
    }

    @Override
    Promise<Balance> getBalance(BalanceId balanceId) {
        def balance = new Balance()
        balance.balanceId = new BalanceId()
        balance.balanceId = balanceId
        balance.status = 'Open'
        return Promise.pure(balance)
    }

    @Override
    Promise<Results<Balance>> getBalances(OrderId orderId) {
        List<Balance> balances = balanceMap.values().findAll { Balance balance ->
            balance.orderIds[0] == orderId
        }.toList()
        Results<Balance> results = new Results<>()
        results.items = balances
        return Promise.pure(results)
    }

    @Override
    Promise<Balance> putBalance(Balance balance) {
        return null
    }

    @Override
    Promise<Balance> adjustItems(Balance balance) {
        return null
    }

    @Override
    Promise<Balance> captureBalance(Balance balance) {
        return null
    }

    @Override
    Promise<Balance> confirmBalance(Balance balance) {
        return null
    }

    @Override
    Promise<Balance> processAsyncBalance(Balance balance) {
        return null
    }

    @Override
    Promise<Balance> checkBalance(Balance balance) {
        return null
    }
}
