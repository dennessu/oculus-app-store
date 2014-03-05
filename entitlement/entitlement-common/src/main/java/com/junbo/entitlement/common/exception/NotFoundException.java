/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.common.exception;

/**
 * Exception subClass.
 */
public class NotFoundException extends EntitlementException {
    private final String entity;
    private final String id;

    public NotFoundException(String entity, Long id) {
        super();
        this.entity = entity;
        this.id = id.toString();
    }

    public String getEntity() {
        return entity;
    }

    public String getId() {
        return id;
    }
}
