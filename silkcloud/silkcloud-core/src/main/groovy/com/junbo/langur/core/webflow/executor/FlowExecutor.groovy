package com.junbo.langur.core.webflow.executor

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import groovy.transform.CompileStatic

/**
 * Created by kg on 2/26/14.
 */
@CompileStatic
interface FlowExecutor {

    Promise<ActionContext> start(String flowId, Map<String, Object> requestScope)

    Promise<ActionContext> resume(String conversationId, String event, Map<String, Object> requestScope)
}
