package com.junbo.order.core.impl.orderaction
import com.junbo.billing.spec.enums.BalanceStatus
import com.junbo.billing.spec.model.Balance
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.clientproxy.FacadeContainer
import com.junbo.order.core.annotation.OrderEventAwareAfter
import com.junbo.order.core.annotation.OrderEventAwareBefore
import com.junbo.order.spec.error.AppErrors
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.apache.commons.collections.CollectionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.annotation.Resource
/**
 * Action for balance status confirmation.
 */
@CompileStatic
@TypeChecked
class ConfirmBalanceAction extends BaseOrderEventAwareAction {
    @Resource(name = 'orderFacadeContainer')
    FacadeContainer facadeContainer

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmBalanceAction)

    @Override
    @OrderEventAwareBefore(action = 'ConfirmBalanceAction')
    @OrderEventAwareAfter(action = 'ConfirmBalanceAction')
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        def order = context.orderServiceContext.order
        return facadeContainer.billingFacade.getBalancesByOrderId(order.id.value).syncRecover { Throwable throwable ->
            LOGGER.error('name=Confirm_Balance_Get_Balances_Error', throwable)
            throw facadeContainer.billingFacade.convertError(throwable).exception()
        }.then { List<Balance> balances ->
            def unconfirmedBalances = balances.findAll { Balance balance ->
                balance.status == BalanceStatus.UNCONFIRMED.name()
            }
            if (unconfirmedBalances == null || CollectionUtils.isEmpty(unconfirmedBalances)) {
                LOGGER.error('name=Confirm_Balance_No_Unconfirmed_Balance')
                throw AppErrors.INSTANCE.balanceConfirmFailed().exception()
            }
            def balanceConfirmed = false
            return Promise.each(unconfirmedBalances) { Balance unconfirmedBalance ->
                return facadeContainer.billingFacade.confirmBalance(unconfirmedBalance)
                        .syncRecover { Throwable throwable ->
                    LOGGER.error('name=Confirm_Balance_Error', throwable)
                    throw facadeContainer.billingFacade.convertError(throwable).exception()
                }.then { Balance confirmedBalance ->
                    if (confirmedBalance.status == BalanceStatus.COMPLETED.name()) {
                        balanceConfirmed = true
                    }
                    return Promise.pure(null)
                }
            }.then {
                if (balanceConfirmed) {
                    return Promise.pure(null)
                }
                LOGGER.error('name=Confirm_Balance_No_Confirmed_Balance')
                throw AppErrors.INSTANCE.balanceConfirmFailed().exception()
            }
        }

    }
}
