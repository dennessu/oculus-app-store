/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.action

import com.junbo.langur.core.IpUtil
import com.junbo.langur.core.context.JunboHttpContext
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.spec.error.AppErrors
import groovy.transform.CompileStatic

/**
 * ValidateIpAddress.
 */
@CompileStatic
class ValidateIpAddress implements Action {
    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        def client = contextWrapper.client
        if (client.ipWhitelist != null && !client.ipWhitelist.isEmpty()) {
            boolean allowed = false
            String ipAddress = JunboHttpContext.requestIpAddress
            for (String ipPattern : client.ipWhitelist) {
                if (IpUtil.match(ipPattern, ipAddress)) {
                    allowed = true
                    break
                }
            }

            if (!allowed) {
                throw AppErrors.INSTANCE.notIpWhitelist(ipAddress).exception()
            }
        }

        return Promise.pure(null)
    }
}
