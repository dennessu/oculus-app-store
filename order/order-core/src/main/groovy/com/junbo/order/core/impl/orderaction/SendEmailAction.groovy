package com.junbo.order.core.impl.orderaction

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.order.clientproxy.catalog.CatalogFacade
import com.junbo.order.clientproxy.email.EmailFacade
import com.junbo.order.clientproxy.identity.IdentityFacade
import groovy.transform.CompileStatic

import javax.annotation.Resource

/**
 * Created by LinYi on 14-3-14.
 */
@CompileStatic
class SendEmailAction implements Action {
    @Resource
    EmailFacade emailFacade

    @Resource
    IdentityFacade identityFacade

    @Resource
    CatalogFacade catalogFacade

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        return null
    }
}
