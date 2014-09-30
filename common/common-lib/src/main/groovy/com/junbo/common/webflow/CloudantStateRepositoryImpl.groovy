/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.webflow
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.util.UUIDUtils
import com.junbo.configuration.topo.DataCenters
import com.junbo.langur.core.context.JunboHttpContext
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
        String id = UUIDUtils.randomUUIDwithDC().toString()

        def conversation = new Conversation(
                id: id,
                ipAddress: JunboHttpContext.requestIpAddress,
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

        ConversationEntity entity = cloudantGetSync(conversationId)
        if (entity == null) {
            int tokenDc = UUIDUtils.getDCFromUUID(conversationId)
            if (DataCenters.instance().isLocalDataCenter(tokenDc)) {
                return null
            }
            def fallbackDbUri = getDbUriByDc(tokenDc)
            if (fallbackDbUri == null) {
                return null
            }
            return wrap((ConversationEntity)getEffective().cloudantGet(fallbackDbUri, entityClass, conversationId).get())
        }
        return wrap(entity)
    }

    @Override
    void persistConversation(Conversation conversation) {
        if (conversation == null) {
            throw new IllegalArgumentException('conversation is null')
        }

        if (conversation.flowStack == null || conversation.flowStack.empty) {
            cloudantDeleteSync(conversation.id)
        } else {
            ConversationEntity existing = cloudantGetSync(conversation.id)

            if (existing == null) {
                cloudantPostSync(unwrap(conversation))
            } else {
                ConversationEntity entity = unwrap(conversation)
                entity.rev = existing.rev
                cloudantPutSync(entity, existing)
            }
        }
    }

    private static Conversation wrap(ConversationEntity entity) {
        if (entity == null) {
            return null
        }

        return new Conversation(
                id: entity.id,
                ipAddress: entity.ipAddress,
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
                ipAddress: entity.ipAddress,
                scope: entity.scope,
                flowStack: entity.flowStack
        )
    }
}
