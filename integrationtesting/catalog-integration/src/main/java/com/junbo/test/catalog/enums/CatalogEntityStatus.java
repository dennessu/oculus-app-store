/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.enums;

/**
 * Enum for catalog entity status.
 *
 * @author Jason
 */
public enum CatalogEntityStatus {
    DRAFT("DRAFT"),
    PENDING_REVIEW("PENDING_REVIEW"),
    REJECTED("REJECTED"),
    APPROVED("APPROVED");

    private String entityStatus;

    private CatalogEntityStatus(String entityStatus) {
        this.entityStatus = entityStatus;
    }

    public String getEntityStatus() {
        return entityStatus;
    }

}
