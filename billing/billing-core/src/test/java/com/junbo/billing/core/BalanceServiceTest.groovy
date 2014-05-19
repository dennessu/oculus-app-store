package com.junbo.billing.core

import com.junbo.billing.core.service.BalanceService
import com.junbo.billing.spec.enums.BalanceStatus
import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.enums.TransactionStatus
import com.junbo.billing.spec.enums.TransactionType
import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.BalanceItem
import com.junbo.billing.spec.model.DiscountItem
import com.junbo.billing.spec.model.Transaction
import com.junbo.common.id.OrderId
import com.junbo.common.id.OrderItemId
import com.junbo.common.id.PaymentInstrumentId
import com.junbo.common.id.UserId
import org.springframework.beans.factory.annotation.Autowired
import org.testng.annotations.Test

/**
 * Created by xmchen on 14-3-14.
 */
class BalanceServiceTest extends BaseTest {

    @Autowired
    BalanceService balanceService

    @Test
    void testManualCaptureBalance() {
        Balance balance = generateBalance(BalanceType.MANUAL_CAPTURE)
        balance = balanceService.addBalance(balance)?.get()

        assert balance != null

        Balance returnedBalance = balanceService.getBalance(balance.balanceId.value)?.get()

        assert returnedBalance != null
        assert returnedBalance.status == BalanceStatus.PENDING_CAPTURE.name()
        assert returnedBalance.getTransactions().size() != 0

        Transaction transaction = returnedBalance.getTransactions()[0]

        assert transaction != null
        assert transaction.status == TransactionStatus.SUCCESS.name()
        assert transaction.type == TransactionType.AUTHORIZE.name()
    }

    @Test
    void testDebitBalance() {
        Balance balance = generateBalance(BalanceType.DEBIT)
        balance = balanceService.addBalance(balance)?.get()

        assert balance != null

        Balance returnedBalance = balanceService.getBalance(balance.balanceId.value)?.get()

        assert returnedBalance != null
        assert returnedBalance.status == BalanceStatus.AWAITING_PAYMENT.name()

        Transaction transaction = returnedBalance.getTransactions()[0]

        assert transaction != null
        assert transaction.status == TransactionStatus.SUCCESS.name()
        assert transaction.type == TransactionType.CHARGE.name()
    }

    @Test
    void testCaptureBalance() {
        Balance balance = generateBalance(BalanceType.MANUAL_CAPTURE)
        balance = balanceService.addBalance(balance)?.get()

        assert balance != null

        Balance captureBalance = new Balance()
        captureBalance.balanceId = balance.balanceId
        captureBalance = balanceService.captureBalance(captureBalance)?.get()

        assert captureBalance != null

        Balance returnedBalance = balanceService.getBalance(balance.balanceId.value)?.get()

        assert returnedBalance != null
        assert returnedBalance.status == BalanceStatus.AWAITING_PAYMENT.name()
        assert returnedBalance.type == BalanceType.MANUAL_CAPTURE.name()

        Transaction transaction = returnedBalance.getTransactions()[0]

        assert transaction != null
        assert transaction.status == TransactionStatus.SUCCESS.name()
        assert transaction.type == TransactionType.CAPTURE.name()
    }

    private Balance generateBalance(BalanceType type) {
        Balance balance = new Balance()
        balance.country = 'US'
        balance.currency = 'USD'
        balance.orderId = new OrderId(12345)
        balance.piId = new PaymentInstrumentId(54321)
        balance.type = type.name()
        balance.userId = new UserId(98765)

        BalanceItem item = new BalanceItem()
        item.orderItemId = new OrderItemId(9999)
        item.financeId = "1234"
        item.amount = 19.99
        DiscountItem discount = new DiscountItem()
        discount.discountAmount = 2.00
        item.addDiscountItem(discount)

        BalanceItem item2 = new BalanceItem()
        item2.orderItemId = new OrderItemId(9998)
        item2.financeId = "1234"
        item2.amount = 3.99

        balance.addBalanceItem(item)
        balance.addBalanceItem(item2)
        balance
    }
}
