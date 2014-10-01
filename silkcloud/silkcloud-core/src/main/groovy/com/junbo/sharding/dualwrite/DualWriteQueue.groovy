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

    public Promise<PendingAction> delete(Object key) {
        PendingAction pendingAction = new PendingAction()
        pendingAction.setDeletedKey(Utils.keyToLong(key))
        pendingAction.setChangedEntityId(pendingAction.getDeletedKey())

        return repository.create(pendingAction);
    }
}
