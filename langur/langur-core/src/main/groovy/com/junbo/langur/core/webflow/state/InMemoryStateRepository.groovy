package com.junbo.langur.core.webflow.state

import groovy.transform.CompileStatic

import java.util.concurrent.ConcurrentHashMap

/**
 * Created by kg on 2/26/14.
 */
@CompileStatic
class InMemoryStateRepository implements StateRepository {

    private final Map<String, Conversation> conversations

    InMemoryStateRepository() {
        this.conversations = new ConcurrentHashMap<>()
    }

    @Override
    Conversation newConversation() {
        String id = UUID.randomUUID().toString()

        def conversation = new Conversation(
                id: id,
                scope: [:],
                flowStack: []
        )

        return conversation
    }

    Conversation loadConversation(String conversationId) {
        if (conversationId == null) {
            throw new IllegalArgumentException('conversationId is null')
        }

        Conversation conversation = conversations.get(conversationId)
        if (conversation == null || conversation.flowStack == null || conversation.flowStack.empty) {
            return null
        }

        return conversation
    }

    void persistConversation(Conversation conversation) {
        if (conversation == null) {
            throw new IllegalArgumentException('conversation is null')
        }

        if (conversation.flowStack == null || conversation.flowStack.empty) {
            conversations.remove(conversation.id)
        } else {
            conversations.put(conversation.id, conversation)
        }
    }
}
