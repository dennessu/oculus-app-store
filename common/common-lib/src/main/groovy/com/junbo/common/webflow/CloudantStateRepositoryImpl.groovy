/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.webflow
import com.junbo.common.cloudant.CloudantClient
import com.junbo.langur.core.webflow.state.Conversation
import com.junbo.langur.core.webflow.state.StateRepository
import groovy.transform.CompileStatic
/**
 * CloudantStateRepositoryImpl.
 */
@CompileStatic
class CloudantStateRepositoryImpl extends CloudantClient<ConversationEntity> implements StateRepository {

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

    @Override
    Conversation loadConversation(String conversationId) {
        if (conversationId == null) {
            throw new IllegalArgumentException('conversationId is null')
        }

        ConversationEntity entity = cloudantGet(conversationId).get()
        return wrap(entity)
    }

    @Override
    void persistConversation(Conversation conversation) {
        if (conversation == null) {
            throw new IllegalArgumentException('conversation is null')
        }

        if (conversation.flowStack == null || conversation.flowStack.empty) {
            super.cloudantDelete(conversation.id).get()
        } else {
            ConversationEntity existing = cloudantGet(conversation.id).get()

            if (existing == null) {
                cloudantPost(unwrap(conversation)).get()
            } else {
                ConversationEntity entity = unwrap(conversation)
                entity.rev = existing.rev
                cloudantPut(entity).get()
            }
        }
    }

    private static Conversation wrap(ConversationEntity entity) {
        if (entity == null) {
            return null
        }

        return new Conversation(
                id: entity.id,
                scope: entity.scope,
                flowStack: entity.flowStack
        )
    }

    private static ConversationEntity unwrap(Conversation entity) {
        if (entity == null) {
            return null
        }

        return new ConversationEntity(
                id: entity.id,
                scope: entity.scope,
                flowStack: entity.flowStack
        )
    }
}
