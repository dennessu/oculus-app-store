package com.junbo.order.core.impl.orderaction

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.core.impl.internal.RiskService
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 * Created by xmchen on 14-7-9.
 */
@CompileStatic
@TypeChecked
@Component('riskUpdateAction')
class RiskUpdateAction implements Action {

    @Resource(name = 'riskService')
    RiskService riskService

    private static final Logger LOGGER = LoggerFactory.getLogger(RiskUpdateAction)

    @Override
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)

        return riskService.updateReview(context.orderServiceContext).syncRecover { Throwable throwable ->
            LOGGER.error('name=Risk_Update_Error', throwable)
            return null
        }.syncThen {
            return null
        }
    }
}
