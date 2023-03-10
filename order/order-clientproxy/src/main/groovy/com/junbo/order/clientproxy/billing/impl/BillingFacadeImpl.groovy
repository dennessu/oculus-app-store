package com.junbo.order.clientproxy.billing.impl
import com.junbo.billing.spec.model.Balance
import com.junbo.billing.spec.resource.BalanceResource
import com.junbo.common.error.AppError
import com.junbo.common.id.BalanceId
import com.junbo.common.id.OrderId
import com.junbo.common.model.Results
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.TransactionHelper
import com.junbo.order.clientproxy.billing.BillingFacade
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.error.ErrorUtils
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

import javax.annotation.Resource
/**
 * Created by chriszhu on 2/19/14.
 */
@CompileStatic
@TypeChecked
@Component('orderBillingFacade')
class BillingFacadeImpl implements BillingFacade {
    @Resource(name = 'order.billingBalanceClient')
    BalanceResource balanceResource

    private static final Logger LOGGER = LoggerFactory.getLogger(BillingFacadeImpl)
    private static final String PAYMENT_INSUFFICIENT_FUND = '121.117'

    @Qualifier('orderTransactionHelper')
    @Autowired
    TransactionHelper transactionHelper

    @Override
    Promise<Balance> createBalance(Balance balance, Boolean isAsyncCharge) {
        return Promise.pure().then {
            balance.isAsyncCharge = isAsyncCharge
            return transactionHelper.executeInNewTransaction {
                return balanceResource.postBalance(balance).recover { Throwable ex ->
                    LOGGER.error('name=BillingFacadeImpl_Create_Balance_Error', ex)
                    throw convertError(ex).exception()
                }.then { Balance b ->
                    if (b == null) {
                        LOGGER.error('name=BillingFacadeImpl_Create_Balance_Null')
                        throw AppErrors.INSTANCE.billingResultInvalid('Create balance response is null').exception()
                    }
                    LOGGER.info('name=BillingFacadeImpl_Create_Balance_Success')
                    return Promise.pure(b)
                }
            }
        }
    }

    @Override
    Promise<Balance> settleBalance(Long balanceId) {
        throw AppErrors.INSTANCE.billingConnectionError('settleBalance is not supported').exception()
    }

    @Override
    Promise<Balance> captureBalance(Balance balance) {
        return transactionHelper.executeInNewTransaction {
            return balanceResource.captureBalance(balance).recover { Throwable ex ->
                LOGGER.error('name=BillingFacadeImpl_Capture_Balance_Error', ex)
                throw convertError(ex).exception()
            }.then { Balance b ->
                if (b == null) {
                    LOGGER.error('name=BillingFacadeImpl_Capture_Balance_Null')
                    throw AppErrors.INSTANCE.billingResultInvalid('Capture balance response is null').exception()
                }
                return Promise.pure(b)
            }
        }
    }

    @Override
    Promise<Balance> getBalanceById(Long balanceId) {
        return balanceResource.getBalance(new BalanceId(balanceId)).recover { Throwable ex ->
            LOGGER.error('name=BillingFacadeImpl_Get_Balance_Error', ex)
            throw convertError(ex).exception()
        }.then { Balance b ->
            return Promise.pure(b)
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    Promise<List<Balance>> getBalancesByOrderId(Long orderId) {
        return transactionHelper.executeInNewTransaction {
            return balanceResource.getBalances(new OrderId(orderId)).recover { Throwable ex ->
                LOGGER.error('name=BillingFacadeImpl_Get_Balances_Error', ex)
                throw convertError(ex).exception()
            }.syncThen { Results<Balance> results ->
                return results == null ? Collections.emptyList() : results.items
            }
        }
    }

    @Override
    Promise<Balance> quoteBalance(Balance balance) {
        return transactionHelper.executeInNewTransaction {
            return balanceResource.quoteBalance(balance).recover { Throwable ex ->
                LOGGER.error('name=BillingFacadeImpl_Order_Quote_Error', ex)
                throw convertToCalculateTaxError(ex).exception()
            }.then { Balance b ->
                if (b == null) {
                    LOGGER.error('name=BillingFacadeImpl_Order_Quote_Null')
                    throw AppErrors.INSTANCE.billingResultInvalid('Billing response is null').exception()
                }
                LOGGER.info('name=BillingFacadeImpl_Order_Quote_Success')
                return Promise.pure(b)
            }
        }
    }

    @Override
    Promise<Balance> confirmBalance(Balance balance) {
        return transactionHelper.executeInNewTransaction {
            return balanceResource.confirmBalance(balance).recover { Throwable ex ->
                LOGGER.error('name=BillingFacadeImpl_Confirm_Balance_Error', ex)
                throw convertError(ex).exception()
            }.then { Balance b ->
                if (b == null) {
                    LOGGER.error('name=BillingFacadeImpl_Confirm_Balance_Null')
                    throw AppErrors.INSTANCE.billingResultInvalid('Confirm balance response is null').exception()
                }
                LOGGER.info('name=BillingFacadeImpl_Confirm_Balance_Success')
                return Promise.pure(b)
            }
        }
    }

    @Override
    Promise<Balance> auditBalance(Balance balance) {
        return transactionHelper.executeInNewTransaction {
            return balanceResource.auditBalance(balance).recover { Throwable ex ->
                LOGGER.error('name=BillingFacadeImpl_Audit_Balance_Error', ex)
                throw convertError(ex).exception()
            }.then { Balance b ->
                if (b == null) {
                    LOGGER.error('name=BillingFacadeImpl_Audit_Balance_Null')
                    throw AppErrors.INSTANCE.billingResultInvalid('Audit balance response is null').exception()
                }
                LOGGER.info('name=BillingFacadeImpl_Audit_Balance_Success')
                return Promise.pure(b)
            }
        }
    }

    @Override
    Promise<Balance> putBalance(Balance balance) {

        return transactionHelper.executeInNewTransaction {
            return balanceResource.putBalance(balance).recover { Throwable ex ->
                LOGGER.error('name=BillingFacadeImpl_Put_Balance_Error', ex)
                throw convertError(ex).exception()
            }.then { Balance b ->
                if (b == null) {
                    LOGGER.error('name=BillingFacadeImpl_Put_Balance_Null')
                    throw AppErrors.INSTANCE.billingResultInvalid('Put balance response is null').exception()
                }
                LOGGER.info('name=BillingFacadeImpl_Put_Balance_Success')
                return Promise.pure(b)
            }
        }
    }

    @Override
    AppError convertError(Throwable error) {
        AppError e = ErrorUtils.toAppError(error)
        if (e != null && e.error().code == PAYMENT_INSUFFICIENT_FUND) {
            // map the specific error to order error
            return AppErrors.INSTANCE.billingInsufficientFund()
        }
        if (e != null) {
            // mapped error from billing. throw it as is
            return e
        }
        // unexpected error from billing. wrap as 500 error
        return AppErrors.INSTANCE.billingConnectionError(error?.message)
    }

    private AppError convertToCalculateTaxError(Throwable error) {
        AppError e = ErrorUtils.toAppError(error)
        if (e != null) {
            return AppErrors.INSTANCE.calculateTaxError(e)
        }
        return AppErrors.INSTANCE.calculateTaxError(error.message)
    }
}
