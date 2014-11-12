/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.dualwrite.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.junbo.common.cloudant.CloudantEntity;
import com.junbo.common.cloudant.CloudantMarshaller;
import groovy.transform.CompileStatic;
import org.springframework.beans.factory.annotation.Required;

import java.io.IOException;

/**
 * The pending action mapper.
 */
@CompileStatic
public class PendingActionMapper {

    private CloudantMarshaller marshaller;

    @Required
    public void setMarshaller(CloudantMarshaller marshaller) {
        this.marshaller = marshaller;
    }

    public PendingActionEntity map(PendingAction pendingAction) {
        PendingActionEntity result = new PendingActionEntity();
        result.setId(pendingAction.getId());

        if (pendingAction.isDeleteAction()) {
            result.setDeletedKey(pendingAction.getDeletedKey());
            result.setSavedEntityType(pendingAction.getDeletedEntityType());
        } else {
            CloudantEntity savedEntity = pendingAction.getSavedEntity();
            if (savedEntity != null) {
                result.setSavedEntityType(savedEntity.getClass().getName());
                try {
                    result.setSavedEntity(marshaller.marshall(savedEntity));
                } catch (JsonProcessingException ex) {
                    throw new RuntimeException("Failed to marshall " + savedEntity.getClass().getName(), ex);
                }
            } else {
                throw new RuntimeException("Save action must have non-null saved entity.");
            }
        }
        result.setChangedEntityId(pendingAction.getChangedEntityId());
        result.setRetryCount(pendingAction.getRetryCount());
        result.setResourceAge(pendingAction.getResourceAge());
        result.setCloudantRev(pendingAction.getCloudantRev());
        result.setCreatedTime(pendingAction.getCreatedTime());
        if (pendingAction.getCreatedBy() != null) {
            result.setCreatedBy(pendingAction.getCreatedBy());
        }
        result.setUpdatedTime(pendingAction.getUpdatedTime());
        if (pendingAction.getUpdatedBy() != null) {
            result.setUpdatedBy(pendingAction.getUpdatedBy());
        }
        result.setDeleted(false);
        return result;
    }

    public PendingAction map(PendingActionEntity pendingAction) {
        if (pendingAction == null) return null;

        PendingAction result = new PendingAction();
        result.setId(pendingAction.getId());

        String savedEntityType = pendingAction.getSavedEntityType();
        String savedEntityJson = pendingAction.getSavedEntity();

        if (savedEntityJson != null) {
            // save action
            try {
                Class cls = Class.forName(savedEntityType);
                result.setSavedEntity((CloudantEntity)marshaller.unmarshall(savedEntityJson, cls));
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException("Failed to find class " + savedEntityType, ex);
            } catch (IOException ex) {
                throw new RuntimeException("Failed to unmarshall " + savedEntityType + " with json: " + savedEntityJson, ex);
            }
        } else {
            // delete action
            result.setDeletedEntityType(savedEntityType);
            result.setDeletedKey(pendingAction.getDeletedKey());
        }

        result.setChangedEntityId(pendingAction.getChangedEntityId());
        result.setRetryCount(pendingAction.getRetryCount());
        result.setResourceAge(pendingAction.getResourceAge());
        result.setCloudantRev(pendingAction.getCloudantRev());
        result.setCreatedTime(pendingAction.getCreatedTime());
        if (pendingAction.getCreatedBy() != null) {
            result.setCreatedBy(pendingAction.getCreatedBy());
        }
        result.setUpdatedTime(pendingAction.getUpdatedTime());
        if (pendingAction.getUpdatedBy() != null) {
            result.setUpdatedBy(pendingAction.getUpdatedBy());
        }
        return result;
    }
}
