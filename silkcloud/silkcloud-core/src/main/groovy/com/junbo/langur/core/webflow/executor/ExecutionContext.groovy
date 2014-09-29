package com.junbo.langur.core.webflow.executor

import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.state.Conversation
import groovy.transform.CompileStatic

/**
 * Created by kg on 2/27/14.
 */
@CompileStatic
class ExecutionContext {

    Conversation conversation

    Map<String, Object> requestScope

    ActionContext newActionContext() {
        return new ActionContext(conversation, requestScope)
    }
}
