package com.junbo.billing.core
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
import groovy.transform.CompileStatic
import org.testng.annotations.Test
/**
 * Created by xmchen on 14-3-14.
 */
@CompileStatic
class BalanceServiceTest extends BaseTest {

    @Test
    void testManualCaptureBalance() {
        Balance balance = generateBalance(BalanceType.MANUAL_CAPTURE)
        balance = balanceService.addBalance(balance)?.get()

        assert balance != null

        Balance returnedBalance = balanceService.getBalance(balance.getId())?.get()

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

        Balance returnedBalance = balanceService.getBalance(balance.getId())?.get()

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
        captureBalance.id = balance.getId()
        captureBalance = balanceService.captureBalance(captureBalance)?.get()

        assert captureBalance != null
        assert captureBalance.status == BalanceStatus.AWAITING_PAYMENT.name()
        assert captureBalance.type == BalanceType.MANUAL_CAPTURE.name()

        Transaction transaction = captureBalance.getTransactions()[1]

        assert transaction != null
        assert transaction.status == TransactionStatus.SUCCESS.name()
        assert transaction.type == TransactionType.CAPTURE.name()
    }

    @Test
    void testFullRefundBalance() {
        Balance balance = generateBalance(BalanceType.DEBIT)
        balance = balanceService.addBalance(balance)?.get()

        assert balance != null

        Balance refundBalance = new Balance()
        refundBalance.userId = new UserId(idGenerator.nextIdByShardId(0))
        refundBalance.originalBalanceId = balance.getId()
        refundBalance.type = BalanceType.REFUND.name()
        refundBalance.trackingUuid = generateUUID()
        refundBalance.piId = new PaymentInstrumentId(idGenerator.nextId(balance.userId.value))
        refundBalance.orderIds = [ new OrderId(idGenerator.nextId(balance.userId.value)) ]
        refundBalance.country = 'US'
        refundBalance.currency = 'USD'
        refundBalance.createdBy = refundBalance.userId.value
        refundBalance.createdTime = new Date()

        balance = balanceService.addBalance(refundBalance)?.get()
        assert balance != null

    }

    private Balance generateBalance(BalanceType type) {
        Balance balance = new Balance()
        balance.trackingUuid = generateUUID()
        balance.country = 'US'
        balance.currency = 'USD'
        balance.userId = new UserId(idGenerator.nextIdByShardId(0))
        balance.orderIds = [ new OrderId(idGenerator.nextId(balance.userId.value)) ]
        balance.piId = new PaymentInstrumentId(idGenerator.nextId(balance.userId.value))
        balance.type = type.name()
        balance.createdBy = balance.userId.value
        balance.createdTime = new Date()

        BalanceItem item = new BalanceItem()
        item.orderItemId = new OrderItemId(idGenerator.nextId(balance.userId.value))
        item.financeId = "1234"
        item.amount = 17.99
        item.createdBy = balance.userId.value
        item.createdTime = new Date()
        DiscountItem discount = new DiscountItem()
        discount.discountAmount = 2.00
        discount.createdTime = new Date()
        discount.createdBy = balance.userId.value
        item.addDiscountItem(discount)

        BalanceItem item2 = new BalanceItem()
        item2.orderItemId = new OrderItemId(idGenerator.nextId(balance.userId.value))
        item2.financeId = "1234"
        item2.amount = 3.99
        item2.createdBy = balance.userId.value
        item2.createdTime = new Date()

        balance.addBalanceItem(item)
        balance.addBalanceItem(item2)

        balance
    }
}
