/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.dualwrite.data
import com.junbo.common.cloudant.CloudantEntity
import com.junbo.common.model.ResourceMeta
import groovy.transform.CompileStatic
/**
 * The pending action.
 */
@CompileStatic
public class PendingAction extends ResourceMeta<Long> {

    private Long id;
    private CloudantEntity savedEntity;
    private Long deletedKey;
    private Long changedEntityId;
    private Integer retryCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    Integer getRetryCount() {
        return retryCount
    }

    void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount
    }
}
