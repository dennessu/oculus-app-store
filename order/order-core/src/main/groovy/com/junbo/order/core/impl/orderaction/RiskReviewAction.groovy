package com.junbo.order.core.impl.orderaction

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.core.impl.common.CoreBuilder
import com.junbo.order.core.impl.internal.RiskService
import com.junbo.order.spec.model.enums.EventStatus
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
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

    @Override
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)

        return riskService.reviewOrder(context.getOrderServiceContext()).syncThen {
            return null
        }
    }
}
