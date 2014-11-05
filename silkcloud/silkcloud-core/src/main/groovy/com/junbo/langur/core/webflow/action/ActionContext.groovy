package com.junbo.langur.core.webflow.action

import com.junbo.langur.core.webflow.FlowException
import com.junbo.langur.core.webflow.state.Conversation
import com.junbo.langur.core.webflow.state.FlowState
import groovy.transform.CompileStatic

/**
 * Created by kg on 2/27/14.
 */
@CompileStatic
class ActionContext {

    private static final String FLASH_SCOPE = 'ACTION_CONTEXT_FLASH_SCOPE'

    private static final String VIEW_SCOPE = 'ACTION_CONTEXT_VIEW_SCOPE'

    private static final String RESPONSE_VIEW = 'ACTION_CONTEXT_RESPONSE_VIEW'

    private final Conversation conversation

    private final Map<String, Object> requestScope

    ActionContext(Conversation conversation, Map<String, Object> requestScope) {
        if (conversation == null) {
            throw new IllegalArgumentException('conversation is null')
        }

        if (requestScope == null) {
            throw new IllegalArgumentException('requestScope is null')
        }

        this.conversation = conversation

        this.requestScope = requestScope
    }

    Map<String, Object> getConversationScope() {
        def scope = conversation.scope
        if (scope == null) {
            conversation.scope = scope = [:]
        }

        return scope
    }

    Map<String, Object> getFlashScope() {
        def scope = (Map<String, Object>) conversationScope[FLASH_SCOPE]
        if (scope == null) {
            conversationScope[FLASH_SCOPE] = scope = [:]
        }

        return scope
    }

    Map<String, Object> getFlowScope() {
        def flowState = currentFlowState
        def scope = flowState.scope
        if (scope == null) {
            flowState.scope = scope = [:]
        }

        return scope
    }

    Map<String, Object> getViewScope() {
        def scope = (Map<String, Object>) flowScope[VIEW_SCOPE]
        if (scope == null) {
            flowScope[VIEW_SCOPE] = scope = [:]
        }

        return scope
    }

    Map<String, Object> getRequestScope() {
        return requestScope
    }

    String getCurrentFlowId() {
        def flowState = currentFlowState
        return flowState.flowId
    }

    String getCurrentStateId() {
        def flowState = currentFlowState
        return flowState.stateId
    }

    String getConversationId() {
        return conversation.id
    }

    Object get(String key) {
        Object value = requestScope.get(key)
        if (value != null) {
            return value
        }

        if (conversation.flowStack != null && !conversation.flowStack.empty) {
            value = viewScope[key] ?: flowScope[key]
            if (value != null) {
                return null
            }
        }

        return flashScope[key] ?: conversationScope[key]
    }

    String getView() {
        return (String) get(RESPONSE_VIEW)
    }

    void setView(String view) {
        requestScope[RESPONSE_VIEW] = view
    }


    private FlowState getCurrentFlowState() {
        if (conversation.flowStack == null || conversation.flowStack.empty) {
            throw new FlowException("no flows in conversation ${conversation.id}")
        }

        return conversation.flowStack.last()
    }
}
