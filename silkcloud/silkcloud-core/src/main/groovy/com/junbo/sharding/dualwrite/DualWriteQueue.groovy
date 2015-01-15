/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.dualwrite
import com.junbo.common.cloudant.CloudantEntity
import com.junbo.common.util.Identifiable
import com.junbo.common.util.Utils
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.data.PendingAction
import com.junbo.sharding.dualwrite.data.PendingActionRepository
import groovy.transform.CompileStatic
/**
 * The cloudant transaction log service.
 * Used to track data changes in cloudant and then dual write to SQL.
 */
@CompileStatic
public class DualWriteQueue {

    private PendingActionRepository repository;

    public DualWriteQueue(PendingActionRepository repository, boolean trackTransactionActions) {
        this.repository = repository;
    }

    public Promise<PendingAction> save(CloudantEntity obj) {
        PendingAction pendingAction = new PendingAction()
        pendingAction.setSavedEntity(obj)
        pendingAction.setChangedEntityId(Utils.keyToLong(((Identifiable)obj).id))
        pendingAction.setRetryCount(0)

        return repository.create(pendingAction);
    }

    public Promise<PendingAction> save(PendingAction pendingAction, CloudantEntity obj) {
        if (pendingAction.changedEntityId != Utils.keyToLong(((Identifiable)obj).id)) {
            throw new RuntimeException(String.format("Update pending action with different ID: %d | %d",
                pendingAction.changedEntityId, Utils.keyToLong(((Identifiable)obj).id)));
        }
        if (pendingAction.getSavedEntity() != null &&
            pendingAction.getSavedEntity().getClass().getName() != obj.getClass().getName()) {
            throw new RuntimeException(String.format("Update pending action with different class: %s | %s",
                    pendingAction.getSavedEntity().getClass().getName(),
                    obj.getClass().getName()));
        }
        PendingAction newPendingAction = new PendingAction();
        newPendingAction.setSavedEntity(obj);
        newPendingAction.setChangedEntityId(pendingAction.changedEntityId);
        newPendingAction.setRetryCount(0);

        return repository.update(newPendingAction, pendingAction);
    }

    public Promise<PendingAction> delete(Object key, Class entityClass) {
        PendingAction pendingAction = new PendingAction()
        pendingAction.setDeletedKey(Utils.keyToLong(key))
        pendingAction.setDeletedEntityType(entityClass.getName())
        pendingAction.setChangedEntityId(pendingAction.getDeletedKey())
        pendingAction.setRetryCount(0)

        return repository.create(pendingAction);
    }

    public Promise<PendingAction> delete(PendingAction pendingAction, Object key) {
        if (pendingAction.changedEntityId != Utils.keyToLong(key)) {
            throw new RuntimeException(String.format("Update pending action with different ID: %d | %d",
                    pendingAction.changedEntityId, Utils.keyToLong(key)));
        }

        PendingAction newPendingAction = new PendingAction()
        newPendingAction.setDeletedKey(Utils.keyToLong(key))
        newPendingAction.setDeletedEntityType(pendingAction.getDeletedEntityType())
        newPendingAction.setChangedEntityId(newPendingAction.getDeletedKey())
        newPendingAction.setRetryCount(0)

        return repository.update(newPendingAction, pendingAction);
    }
}
