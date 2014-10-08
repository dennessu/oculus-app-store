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
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * CloudantStateRepositoryImpl.
 */
@CompileStatic
class CloudantStateRepositoryImpl extends CloudantClient<ConversationEntity> implements StateRepository {
    private static final Logger logger = LoggerFactory.getLogger(CloudantStateRepositoryImpl.class);

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

        // load conversation from home shard
        int tokenDc = UUIDUtils.getDCFromUUID(conversationId)
        if (!DataCenters.instance().isLocalDataCenter(tokenDc)) {
            logger.info("Routing to remote shard {} for cid {}", tokenDc, conversationId)
        }
        def dbUri = getDbUriByDc(tokenDc)
        return wrap((ConversationEntity)getEffective().cloudantGet(dbUri, entityClass, conversationId).get())
    }

    @Override
    void persistConversation(Conversation conversation) {
        if (conversation == null) {
            throw new IllegalArgumentException('conversation is null')
        }

        // load conversation from home shard
        int tokenDc = UUIDUtils.getDCFromUUID(conversation.id)
        if (!DataCenters.instance().isLocalDataCenter(tokenDc)) {
            logger.info("Routing to remote shard {} for cid {}", tokenDc, conversation.id)
        }
        def dbUri = getDbUriByDc(tokenDc)
        def effective = getEffective()

        if (conversation.flowStack == null || conversation.flowStack.empty) {
            effective.cloudantGet(dbUri, entityClass, conversation.id).then { ConversationEntity entity ->
                return effective.cloudantDelete(dbUri, entityClass, entity)
            }.get()
        } else {
            ConversationEntity existing = (ConversationEntity)effective.cloudantGet(dbUri, entityClass, conversation.id).get()

            if (existing == null) {
                effective.cloudantPost(dbUri, entityClass, unwrap(conversation)).get()
            } else {
                ConversationEntity entity = unwrap(conversation)
                entity.rev = existing.rev
                effective.cloudantPut(dbUri, entityClass, entity).get()
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
