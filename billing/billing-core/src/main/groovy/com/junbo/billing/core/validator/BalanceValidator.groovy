package com.junbo.billing.core.validator

import com.junbo.billing.clientproxy.IdentityFacade
import com.junbo.billing.clientproxy.PaymentFacade
import com.junbo.billing.core.service.CurrencyService
import com.junbo.billing.db.repository.BalanceRepository
import com.junbo.billing.spec.enums.BalanceStatus
import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.error.AppErrors
import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.BalanceItem
import com.junbo.billing.spec.model.Transaction
import com.junbo.common.id.BalanceId
import com.junbo.common.id.PaymentInstrumentId
import com.junbo.common.id.UserId
import com.junbo.identity.spec.v1.model.User
import com.junbo.langur.core.promise.Promise
import com.junbo.payment.spec.model.PaymentInstrument
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier

/**
 * Created by xmchen on 14-4-29.
 */
@CompileStatic
class BalanceValidator {
    @Autowired
    @Qualifier(value='billingIdentityFacade')
    IdentityFacade identityFacade

    @Autowired
    @Qualifier(value='billingPaymentFacade')
    PaymentFacade paymentFacade

    @Autowired
    CurrencyService currencyService

    @Autowired
    BalanceRepository balanceRepository

    private static final Logger LOGGER = LoggerFactory.getLogger(BalanceValidator)

    Promise<User> validateUser(UserId userId) {
        if (userId == null) {
            throw AppErrors.INSTANCE.fieldMissingValue('userId').exception()
        }

        Long id = userId.value
        return identityFacade.getUser(id).recover { Throwable throwable ->
            LOGGER.error('name=Error_Get_User. user id: ' + id, throwable)
            throw AppErrors.INSTANCE.userNotFound(id.toString()).exception()
        }.then { User user ->
            if (user == null) {
                LOGGER.error('name=Error_Get_User. Get null for the user id: {}', id)
                throw AppErrors.INSTANCE.userNotFound(id.toString()).exception()
            }
            if (user.status == null || user.status != 'ACTIVE') {
                LOGGER.error('name=Error_Get_User. User not active with id: {}', id)
                throw AppErrors.INSTANCE.userStatusInvalid(id.toString()).exception()
            }
            return Promise.pure(user)
        }
    }

    Promise<PaymentInstrument> validatePI(PaymentInstrumentId piId) {
        if (piId == null) {
            throw AppErrors.INSTANCE.fieldMissingValue('piId').exception()
        }
        return paymentFacade.getPaymentInstrument(piId.value).recover { Throwable throwable ->
            LOGGER.error('name=Error_Get_PaymentInstrument. pi id: ' + piId.value, throwable)
            throw AppErrors.INSTANCE.piNotFound(piId.value.toString()).exception()
        }.then { PaymentInstrument pi ->
            //todo: more validation for the PI
            return Promise.pure(pi)
        }
    }

    void validateBalanceType(String type) {
        if (type == null || type.isEmpty()) {
            throw AppErrors.INSTANCE.fieldMissingValue('type').exception()
        }

        try {
            BalanceType.valueOf(type)
        }
        catch (IllegalArgumentException ex) {
            throw AppErrors.INSTANCE.invalidBalanceType(type).exception()
        }
    }

    void validateCurrency(String currency) {
        if (currency == null || currency.isEmpty()) {
            throw AppErrors.INSTANCE.fieldMissingValue('currency').exception()
        }
        if (!currencyService.exists(currency)) {
            throw AppErrors.INSTANCE.currencyNotFound(currency).exception()
        }
    }

    void validateCountry(String country) {
        if (country == null || country.isEmpty()) {
            throw AppErrors.INSTANCE.fieldMissingValue('country').exception()
        }
        //todo: more validation for country
    }

    void validateBalance(Balance balance, Boolean isQuote) {
        if (!isQuote && balance.orderId == null) {
            throw AppErrors.INSTANCE.fieldMissingValue('orderId').exception()
        }
        if (balance.balanceItems == null || balance.balanceItems.size() == 0) {
            throw AppErrors.INSTANCE.fieldMissingValue('balanceItems').exception()
        }
        balance.balanceItems.each { BalanceItem balanceItem ->
            if (!isQuote) {
                if (balanceItem.orderItemId == null) {
                    throw AppErrors.INSTANCE.fieldMissingValue('balanceItem.orderItemId').exception()
                }
                if (balanceItem.orderId == null) {
                    balanceItem.orderId = balance.orderId
                }
            }
            if (balanceItem.amount == null) {
                throw AppErrors.INSTANCE.fieldMissingValue('balanceItem.amount').exception()
            }
        }
    }

    void validateBalanceTotal(Balance balance) {
        if (balance.totalAmount <= 0) {
            throw AppErrors.INSTANCE.invalidBalanceTotal(balance.totalAmount.toString()).exception()
        }

        if (balance.type == BalanceType.REFUND.name()) {
            Balance originalBalance = balanceRepository.getBalance(balance.originalBalanceId.value)
            List<Balance> refundeds = balanceRepository.getRefundBalancesByOriginalId(balance.originalBalanceId.value)
            BigDecimal totalRefunded
            for (Balance refunded : refundeds) {
                totalRefunded += refunded.totalAmount
            }
            if (totalRefunded + balance.totalAmount > originalBalance.totalAmount) {
                LOGGER.error('for the original balance {}, refund total {} exceeded, original amount {}, refunded {}',
                    originalBalance.balanceId, balance.totalAmount, originalBalance.totalAmount, totalRefunded)
                throw AppErrors.INSTANCE.balanceRefundTotalExceeded(balance.totalAmount, originalBalance.totalAmount,
                totalRefunded).exception()
            }
            for (BalanceItem refundItem : balance.balanceItems) {
                BalanceItem originalItem = originalBalance.getBalanceItem(refundItem.originalBalanceItemId)
                BigDecimal itemRefunded
                for (Balance refunded : refundeds) {
                    for (BalanceItem refundedItem : refunded.balanceItems) {
                        if (refundedItem.originalBalanceItemId == refundItem.originalBalanceItemId) {
                            itemRefunded += refundedItem.amount
                            //todo: think about the refund tax
                        }
                    }
                }
                if (itemRefunded + refundItem.amount > originalItem.amount) {
                    LOGGER.error('for the original balance item {}, refund amount {} exceeded, original amount {}, ' +
                            'refunded {}', refundItem.originalBalanceItemId, refundItem.amount,
                            originalItem.amount, itemRefunded)
                    throw AppErrors.INSTANCE.balanceItemRefundTotalExceeded(refundItem.amount,
                            originalItem.amount, itemRefunded).exception()
                }
            }
        }
    }

    Balance validateBalanceId(BalanceId balanceId) {
        if (balanceId == null) {
            throw AppErrors.INSTANCE.fieldMissingValue('balanceId').exception()
        }
        Balance savedBalance = balanceRepository.getBalance(balanceId.value)
        if (savedBalance == null) {
            throw AppErrors.INSTANCE.balanceNotFound(balanceId.value.toString()).exception()
        }
        return savedBalance
    }

    void validateBalanceStatus(String status, String expectedStatus) {
        if (status != expectedStatus) {
            throw AppErrors.INSTANCE.invalidBalanceStatus(status, expectedStatus).exception()
        }
    }

    void validateBalanceStatus(String status, Collection<String> expectedStatus) {
        if (!expectedStatus.contains(status)) {
            throw AppErrors.INSTANCE.invalidBalanceStatus(status, expectedStatus.join(',')).exception()
        }
    }

    void validateTransactionNotEmpty(BalanceId balanceId, Collection<Transaction> transactions) {
        if (transactions == null || transactions.size() == 0) {
            throw AppErrors.INSTANCE.transactionNotFound(balanceId.value.toString()).exception()
        }
    }

    void validateRefund(Balance balance) {
        if (balance.originalBalanceId == null) {
            throw AppErrors.INSTANCE.fieldMissingValue('originalBalanceId').exception()
        }

        if (balance.balanceItems == null || balance.balanceItems.size() == 0) {
            throw AppErrors.INSTANCE.fieldMissingValue('balanceItems').exception()
        }

        validateBalanceStatus(balance.status, [BalanceStatus.COMPLETED.name(), BalanceStatus.AWAITING_PAYMENT.name()])

        for (BalanceItem item : balance.balanceItems) {
            if (item.originalBalanceItemId == null) {
                throw AppErrors.INSTANCE.fieldMissingValue('balanceItems.originalBalanceItemId').exception()
            }
        }
    }
}
