package com.junbo.langur.core.webflow.state

import groovy.transform.CompileStatic

/**
 * The DummyStateRepository class.
 */
@CompileStatic
class DummyStateRepository implements StateRepository {

    @Override
    Conversation newConversation() {
        return genConversation()
    }

    @Override
    Conversation loadConversation(String conversationId) {
        return genConversation()
    }

    @Override
    void persistConversation(Conversation conversation) {
    }

    private Conversation genConversation() {
        Conversation conversation = new Conversation()
        conversation.id = UUID.randomUUID().toString()
        conversation.flowStack = [] as List
        conversation.scope = [:]
        return conversation
    }
}
