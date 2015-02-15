/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.dualwrite

import com.junbo.common.cloudant.CloudantEntity
import com.junbo.common.util.Identifiable
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.transaction.AsyncTransactionTemplate
import com.junbo.sharding.dualwrite.data.PendingAction
import com.junbo.sharding.dualwrite.data.PendingActionRepository
import com.junbo.sharding.repo.BaseRepository
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition

/**
 * Replays the pending action
 */
@CompileStatic
public class PendingActionReplayer {
    private static final Logger logger = LoggerFactory.getLogger(PendingActionReplayer.class);

    private BaseRepository repository;
    private PendingActionRepository pendingActionRepository;
    private AsyncTransactionTemplate transactionTemplate;
    private Class entityType;

    public PendingActionReplayer(BaseRepository repository, PendingActionRepository pendingActionRepository, PlatformTransactionManager transactionManager, Class entityType) {
        this.repository = repository;
        this.pendingActionRepository = pendingActionRepository;
        this.transactionTemplate = new AsyncTransactionTemplate(transactionManager);
        this.transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        this.entityType = entityType;
    }

    public Class getEntityType() {
        return entityType
    }

    public Promise<Void> replay(PendingAction action) {
        logger.info("Replaying action {} changed entity id is {}", action.id, action.changedEntityId)
        return transactionTemplate.execute {
            Promise<Boolean> future;
            if (action.isSaveAction()) {
                future = replaySave(action.getSavedEntity());
            } else if (action.isDeleteAction()) {
                future = replayDelete(action.getDeletedKey());
            }

            assert future != null: "Unexpected action, not saveAction nor deleteAction";

            return future.then { Boolean result ->
                if (result != null && result.booleanValue()) {
                    return pendingActionRepository.delete(action.id)
                }
                return Promise.pure(null);
            }
        }
    }

    private Promise<Boolean> replaySave(CloudantEntity entity) {
        assert entity instanceof Identifiable

        Object key = ((Identifiable)entity).id ?: entity.cloudantId
        return repository.get(key).then { CloudantEntity savedEntity ->
            if (savedEntity == null) {
                return repository.create(entity);
            }
            if (entity.resourceAge == null || savedEntity.resourceAge == null) {
                logger.error("ResourceAge is null for dual-write resource. Id: ${entity.id}");
                throw new RuntimeException("ResourceAge is null for entity.")
            }
            boolean needUpdate = false
            if (entity.resourceAge == savedEntity.resourceAge) {
                if (entity.updatedTime == savedEntity.updatedTime) {
                    // update just to be defensive
                    logger.warn("Dualwrite overwrite resource {}:{} with same resourceAge same updatedTime: {}",
                            entity.getClass(), entity.cloudantId, entity.resourceAge);
                    needUpdate = true;
                } else {
                    if ((entity.updatedTime == null && savedEntity.updatedTime != null) ||
                        (entity.updatedTime != null && savedEntity.updatedTime == null) ||
                        (entity.updatedTime.after(savedEntity.updatedTime))) {
                        logger.warn("Dualwrite overwrite resource {}:{} with same resourceAge: {}, updatedTimeNew: {}, updatedTimeNew: {}",
                                entity.getClass(), entity.cloudantId, entity.resourceAge, entity.updatedTime, savedEntity.updatedTime);
                        needUpdate = true;
                    } else {
                        logger.warn("Dualwrite skip resource {}:{} with same resourceAge: {}, updatedTimeNew: {}, updatedTimeNew: {}",
                                entity.getClass(), entity.cloudantId, entity.resourceAge, entity.updatedTime, savedEntity.updatedTime);
                    }
                }
            } else if (entity.resourceAge > savedEntity.resourceAge) {
                needUpdate = true;
            }

            if (needUpdate) {
                entity.cloudantRev = savedEntity.cloudantRev
                return repository.update(entity, savedEntity);
            }
            return Promise.pure(entity);
        }.then {
            return Promise.pure(true);
        }.recover { Throwable ex ->
            // whatever exception, we log and return false
            logger.error("Failed to replaySave of entity [$key] using repository type [${repository.class.name}]", ex);
            return Promise.pure(false);
        }
    }

    private Promise<Boolean> replayDelete(Object key) {
        return repository.delete(key).then {
            return Promise.pure(true);
        }.recover { Throwable ex ->
            // whatever exception, we log and return false
            logger.error("Failed to replayDelete of entity [${key}] using repository type [${repository.class.name}]", ex);
            return Promise.pure(false);
        }
    }
}
