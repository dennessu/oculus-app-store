package com.junbo.order.core.impl.orderaction
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.core.impl.internal.OrderInternalService
import com.junbo.order.db.repo.facade.OrderRepositoryFacade
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

import javax.annotation.Resource
/**
 * Created by fzhang on 14-3-25.
 */
@CompileStatic
@TypeChecked
class MarkSettleAction implements Action {

    @Resource(name = 'orderRepositoryFacade')
    OrderRepositoryFacade repo
    @Autowired
    OrderInternalService orderInternalService

    @Override
    @Transactional
    Promise<ActionResult> execute(ActionContext actionContext) {
        def context = ActionUtils.getOrderActionContext(actionContext)
        def order = context.orderServiceContext.order

        orderInternalService.markSettlement(order)
        return Promise.pure(null)
    }
}
