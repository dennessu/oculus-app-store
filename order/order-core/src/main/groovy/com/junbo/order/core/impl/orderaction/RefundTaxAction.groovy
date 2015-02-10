package com.junbo.order.core.impl.orderaction

import com.junbo.billing.spec.enums.BalanceType
import com.junbo.billing.spec.enums.PropertyKey
import com.junbo.billing.spec.model.Balance
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.clientproxy.FacadeContainer
import com.junbo.order.core.impl.common.CoreBuilder
import com.junbo.order.core.impl.common.CoreUtils
import com.junbo.order.core.impl.internal.OrderInternalService
import com.junbo.order.core.impl.order.OrderServiceContextBuilder
import com.junbo.order.db.repo.facade.OrderRepositoryFacade
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.enums.EventStatus
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional

import javax.annotation.Resource

/**
 * Created by xmchen on 15/2/9.
 */
@CompileStatic
@TypeChecked
class RefundTaxAction extends BaseOrderEventAwareAction {
    @Resource(name = 'orderFacadeContainer')
    FacadeContainer facadeContainer

    @Resource(name = 'orderInternalService')
    OrderInternalService orderInternalService

    @Resource(name = 'orderServiceContextBuilder')
    OrderServiceContextBuilder orderServiceContextBuilder

    @Resource(name = 'orderRepositoryFacade')
    OrderRepositoryFacade orderRepository

    private static final Logger LOGGER = LoggerFactory.getLogger(RefundTaxAction)


    @Override
    @Transactional
    Promise<ActionResult> doExecute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        def order = context.orderServiceContext.order
        CoreUtils.readHeader(order, context?.orderServiceContext?.apiContext)

        Balance balance = CoreBuilder.buildBalance(context.orderServiceContext.order, BalanceType.DEBIT)
        balance.propertySet.put(PropertyKey.EXEMPT_REASON.name(), "EDUCATION")
        return facadeContainer.billingFacade.quoteBalance(balance).syncRecover {
            Throwable throwable ->
                LOGGER.error('name=Fail_To_Quote_Balance', throwable)
                throw throwable
        }.then { Balance taxedBalance ->
            if (taxedBalance == null) {
                LOGGER.info('name=Fail_To_Calculate_Tax_Balance_Not_Found')
                throw AppErrors.INSTANCE.balanceNotFound().exception()
            }

            if (taxedBalance.taxAmount > order.totalTax) {
                LOGGER.error('name=Tax_Is_More_Than_Before')
                return Promise.pure(CoreBuilder.buildActionResultForOrderEventAwareAction(context, EventStatus.FAILED))
            }

            if (taxedBalance.taxAmount.equals(order.totalTax)) {
                LOGGER.info('name=No_Tax_To_Refund')
                return Promise.pure(CoreBuilder.buildActionResultForOrderEventAwareAction(context, EventStatus.FAILED))
            }

            CoreBuilder.fillTaxInfo(order, taxedBalance)
            return orderInternalService.refundOrCancelOrder(order, context.orderServiceContext).syncThen { Order o ->
                context.orderServiceContext.order = o
                return CoreBuilder.buildActionResultForOrderEventAwareAction(context, EventStatus.COMPLETED)
            }
        }
    }
}
