/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.dualwrite
import com.junbo.common.cloudant.CloudantEntity
import com.junbo.common.util.Context
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
    private boolean trackTransactionActions;

    public DualWriteQueue(PendingActionRepository repository, boolean trackTransactionActions) {
        this.repository = repository;
        this.trackTransactionActions = trackTransactionActions;
    }

    public Promise<Void> save(CloudantEntity obj) {
        PendingAction pendingAction = new PendingAction()
        pendingAction.setSavedEntity(obj)
        pendingAction.setChangedEntityId(Utils.keyToLong(((Identifiable)obj).id))

        return repository.create(pendingAction).then { PendingAction entity ->
            if (trackTransactionActions) {
                getPendingActions().add(pendingAction);
            }
        }
    }

    public Promise<Void> delete(Object key) {
        PendingAction pendingAction = new PendingAction()
        pendingAction.setDeletedKey(Utils.keyToLong(key))
        pendingAction.setChangedEntityId(pendingAction.getDeletedKey())

        return repository.create(pendingAction).then { PendingAction entity ->
            if (trackTransactionActions) {
                getPendingActions().add(pendingAction);
            }
        }
    }

    /**
     * Get the pending actions.
     * @return The list of pending actions.
     */
    public static List<PendingAction> getPendingActions() {
        // Note: We put pendingActions to Context() to leverage the fact that Context() is reset in every request.
        if (Context.get().getPendingActions() == null) {
            Context.get().setPendingActions(new ArrayList<>())
        }
        return (List<PendingAction>)Context.get().getPendingActions();
    }

    public static void clear() {
        Context.get().setPendingActions(null);
    }
}
