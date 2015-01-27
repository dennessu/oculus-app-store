package com.junbo.order.mock
import com.junbo.billing.spec.enums.BalanceStatus
import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.BalanceItem
import com.junbo.billing.spec.resource.BalanceResource
import com.junbo.common.id.BalanceId
import com.junbo.common.id.OrderId
import com.junbo.common.model.Results
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import java.util.concurrent.ConcurrentHashMap
/**
 * Created by chriszhu on 2/20/14.
 */
@CompileStatic
@Component('mockBalanceResource')
class MockBalanceResource extends BaseMock implements BalanceResource {

    private final Map<BalanceId, Balance> balanceMap = new ConcurrentHashMap<>()

    @Override
    Promise<Balance> postBalance(Balance balance) {
        balance.id = new BalanceId(generateLong())
        balance.status = BalanceStatus.COMPLETED
        balance.totalAmount = 0G
        balance.taxAmount = 0G
        balance.discountAmount = 0G
        balance.balanceItems.each { BalanceItem bi ->
            balance.totalAmount += bi.amount
            bi.taxAmount = 1.00G
            bi.discountAmount = 0G
            balance.taxAmount += bi.taxAmount
        }
        balanceMap[balance.getId()] = balance
        if (balance.cancelRedirectUrl == 'exception') {
            throw new Exception('mock exception')
        }
        if (balance.cancelRedirectUrl == 'decline') {
            balance.status = BalanceStatus.FAILED
        }
        return Promise.pure(balance)
    }

    @Override
    Promise<Balance> quoteBalance(Balance balance) {
        balance.id = new BalanceId(generateLong())
        balance.status = null
        balance.taxAmount = 2.00G
        balance.taxIncluded = false
        balance.balanceItems.each { BalanceItem bi ->
            bi.taxAmount = 1.00G
        }
        return Promise.pure(balance)
    }

    @Override
    Promise<Balance> getBalance(BalanceId balanceId) {
        def balance = balanceMap[balanceId]
        if(balance == null) {
            balance = new Balance()

            balance.id = new BalanceId()
            balance.id = balanceId
        }
        balance.status = BalanceStatus.COMPLETED
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
    Promise<Balance> auditBalance(Balance balance) {
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
