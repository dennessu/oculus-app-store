/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.dualwrite.data

import com.junbo.common.cloudant.CloudantEntity
import com.junbo.common.model.ResourceMeta
import com.junbo.common.util.Identifiable
import groovy.transform.CompileStatic

import javax.transaction.Transaction

/**
 * The pending action.
 */
@CompileStatic
public class PendingAction extends ResourceMeta implements Identifiable<UUID> {

    private UUID id;
    private CloudantEntity savedEntity;
    private Long deletedKey;
    private Long changedEntityId;

    private Transaction transaction;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public CloudantEntity getSavedEntity() {
        return savedEntity;
    }

    public void setSavedEntity(CloudantEntity savedEntity) {
        this.savedEntity = savedEntity;
    }

    public Long getDeletedKey() {
        return deletedKey;
    }

    public void setDeletedKey(Long deletedKey) {
        this.deletedKey = deletedKey;
    }

    public Long getChangedEntityId() {
        return changedEntityId;
    }

    public void setChangedEntityId(Long changedEntityId) {
        this.changedEntityId = changedEntityId;
    }

    public boolean isSaveAction() {
        return this.savedEntity != null;
    }

    public boolean isDeleteAction() {
        return this.deletedKey != null;
    }

    Transaction getTransaction() {
        return transaction
    }

    void setTransaction(Transaction transaction) {
        this.transaction = transaction
    }
}
