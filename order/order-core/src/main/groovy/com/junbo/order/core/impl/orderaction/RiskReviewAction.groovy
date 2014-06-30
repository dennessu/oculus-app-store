package com.junbo.order.core.impl.orderaction

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.core.impl.common.CoreBuilder
import com.junbo.order.core.impl.internal.RiskReviewResult
import com.junbo.order.core.impl.internal.RiskService
import com.junbo.order.spec.model.enums.EventStatus
import com.junbo.order.spec.model.enums.OrderStatus
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 * Created by xmchen on 14-6-23.
 */
@CompileStatic
@TypeChecked
@Component('riskReviewAction')
class RiskReviewAction implements Action {

    @Resource(name = 'riskService')
    RiskService riskService

    private static final Logger LOGGER = LoggerFactory.getLogger(RiskReviewAction)

    @Override
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)

        return riskService.reviewOrder(context.orderServiceContext).syncRecover { Throwable throwable ->
            LOGGER.error('name=Risk_Review_Error', throwable)
            // return approved when risk calling failed
            return RiskReviewResult.APPROVED
        }.syncThen { RiskReviewResult result ->
            if (result == RiskReviewResult.REJECT) {
                return new ActionResult('REJECT')
            } else {
                return new ActionResult('APPROVED')
            }
        }
    }
}
