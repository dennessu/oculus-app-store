package com.junbo.billing.core.validator

import com.junbo.billing.clientproxy.CountryFacade
import com.junbo.billing.clientproxy.CurrencyFacade
import com.junbo.billing.clientproxy.IdentityFacade
import com.junbo.billing.clientproxy.PaymentFacade
import com.junbo.billing.db.repo.facade.BalanceRepositoryFacade
import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.error.AppErrors
import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.model.BalanceItem
import com.junbo.billing.spec.model.Transaction
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.BalanceId
import com.junbo.common.id.PaymentInstrumentId
import com.junbo.common.id.UserId
import com.junbo.common.shuffle.Oculus48Id
import com.junbo.identity.spec.v1.model.Country
import com.junbo.identity.spec.v1.model.Currency;
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
    IdentityFacade identityFacade

    PaymentFacade paymentFacade

    CurrencyFacade currencyFacade

    CountryFacade countryFacade

    @Autowired
    void setIdentityFacade(@Qualifier('billingIdentityFacade')IdentityFacade identityFacade) {
        this.identityFacade = identityFacade
    }

    @Autowired
    void setPaymentFacade(@Qualifier('billingPaymentFacade')PaymentFacade paymentFacade) {
        this.paymentFacade = paymentFacade
    }

    @Autowired
    void setCurrencyFacade(@Qualifier('billingCurrencyFacade')CurrencyFacade currencyFacade) {
        this.currencyFacade = currencyFacade
    }

    @Autowired
    void setCountryFacade(@Qualifier('billingCountryFacade')CountryFacade countryFacade) {
        this.countryFacade = countryFacade
    }

    @Autowired
    BalanceRepositoryFacade balanceRepositoryFacade

    private static final Logger LOGGER = LoggerFactory.getLogger(BalanceValidator)

    Promise<User> validateUser(UserId userId) {
        if (userId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('userId').exception()
        }

        Long id = userId.value
        return identityFacade.getUser(id).recover { Throwable throwable ->
            LOGGER.error('name=Error_Get_User. user id: ' + id, throwable)
            throw AppErrors.INSTANCE.userNotFound("user", userId).exception()
        }.then { User user ->
            if (user == null) {
                LOGGER.error('name=Error_Get_User. Get null for the user id: {}', id)
                throw AppErrors.INSTANCE.userNotFound("user", userId).exception()
            }
            if (user.status == null || user.status != 'ACTIVE') {
                LOGGER.error('name=Error_Get_User. User not active with id: {}', id)
                throw AppErrors.INSTANCE.userStatusInvalid("status", userId, user.status).exception()
            }
            return Promise.pure(user)
        }
    }

    Promise<PaymentInstrument> validatePI(PaymentInstrumentId piId) {
        if (piId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('paymentInstrumentId').exception()
        }
        return paymentFacade.getPaymentInstrument(piId.value).recover { Throwable throwable ->
            LOGGER.error('name=Error_Get_PaymentInstrument. pi id: ' + piId.value, throwable)
            throw AppErrors.INSTANCE.piNotFound("paymentInstrumentId", piId).exception()
        }.then { PaymentInstrument pi ->
            //todo: more validation for the PI
            return Promise.pure(pi)
        }
    }

    void validateBalanceType(String type) {
        if (type == null || type.isEmpty()) {
            throw AppCommonErrors.INSTANCE.fieldRequired('type').exception()
        }

        try {
            BalanceType.valueOf(type)
        }
        catch (IllegalArgumentException ex) {
            throw AppErrors.INSTANCE.invalidBalanceType(type).exception()
        }
    }

    Promise<Currency> validateCurrency(String currency) {
        if (currency == null || currency.isEmpty()) {
            throw AppCommonErrors.INSTANCE.fieldRequired('currency').exception()
        }

        return currencyFacade.getCurrency(currency).recover { Throwable throwable ->
            LOGGER.error('error in get currency: ' + currency, throwable)
            throw AppErrors.INSTANCE.currencyNotFound(currency).exception()
        }.then { Currency cur ->
            return Promise.pure(cur)
        }

    }

    Promise<Country> validateCountry(String country) {
        if (country == null || country.isEmpty()) {
            throw AppCommonErrors.INSTANCE.fieldRequired('country').exception()
        }
        return countryFacade.getCountry(country).recover { Throwable throwable ->
            LOGGER.error('error in get country: ' + country, throwable)
            throw AppErrors.INSTANCE.countryNotFound(country).exception()
        }.then { Country c ->
            return Promise.pure(c)
        }
    }

    void validateBalance(Balance balance, Boolean isQuote) {
        if (!isQuote && (balance.orderIds == null || balance.orderIds.size() == 0)) {
            throw AppCommonErrors.INSTANCE.fieldRequired('orderIds').exception()
        }
        if (balance.balanceItems == null || balance.balanceItems.size() == 0) {
            throw AppCommonErrors.INSTANCE.fieldRequired('balanceItems').exception()
        }
        balance.balanceItems.each { BalanceItem balanceItem ->
            if (!isQuote) {
                if (balanceItem.orderItemId == null) {
                    throw AppCommonErrors.INSTANCE.fieldRequired('balanceItem.orderItemId').exception()
                }
                if (balanceItem.orderId == null) {
                    balanceItem.orderId = balance.orderIds[0]
                }
            }
            if (balanceItem.amount == null) {
                throw AppCommonErrors.INSTANCE.fieldRequired('balanceItem.amount').exception()
            }
        }
    }

    void validateBalanceTotal(Balance balance) {
        if (balance.totalAmount <= 0) {
            throw AppErrors.INSTANCE.invalidBalanceTotal(balance.totalAmount.toString()).exception()
        }

        if (balance.type == BalanceType.REFUND.name()) {
            Balance originalBalance = balanceRepositoryFacade.getBalance(balance.originalBalanceId.value)
            List<Balance> refundeds = balanceRepositoryFacade.getRefundBalancesByOriginalId(balance.originalBalanceId.value)
            BigDecimal totalRefunded = 0
            for (Balance refunded : refundeds) {
                totalRefunded += refunded.totalAmount
            }
            if (totalRefunded + balance.totalAmount > originalBalance.totalAmount) {
                LOGGER.error('for the original balance {}, refund total {} exceeded, original amount {}, refunded {}',
                    originalBalance.id, balance.totalAmount, originalBalance.totalAmount, totalRefunded)
                throw AppErrors.INSTANCE.balanceRefundTotalExceeded(originalBalance.getId(), balance.totalAmount, originalBalance.totalAmount,
                totalRefunded).exception()
            }
            for (BalanceItem refundItem : balance.balanceItems) {
                BalanceItem originalItem = originalBalance.getBalanceItem(refundItem.originalBalanceItemId)
                BigDecimal itemRefunded = 0
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
                    throw AppErrors.INSTANCE.balanceItemRefundTotalExceeded(
                            originalBalance.getId(), Oculus48Id.encode(originalItem.getId()),
                            refundItem.amount, originalItem.amount, itemRefunded).exception()
                }
            }
        }
    }

    Balance validateBalanceId(BalanceId balanceId) {
        if (balanceId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('balanceId').exception()
        }
        Balance savedBalance = balanceRepositoryFacade.getBalance(balanceId.value)
        if (savedBalance == null) {
            throw AppCommonErrors.INSTANCE.resourceNotFound("Balance", balanceId).exception()
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
            throw AppErrors.INSTANCE.transactionNotFound(balanceId).exception()
        }
    }

    void validateRefund(Balance balance) {
        if (balance.originalBalanceId == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('originalBalanceId').exception()
        }

        if (balance.balanceItems != null) {
            for (BalanceItem item : balance.balanceItems) {
                if (item.originalBalanceItemId == null) {
                    throw AppCommonErrors.INSTANCE.fieldRequired('balanceItems.originalBalanceItemId').exception()
                }
                if (item.amount == null) {
                    throw AppCommonErrors.INSTANCE.fieldRequired('balanceItems.amount').exception()
                }
            }
        }
    }
}
