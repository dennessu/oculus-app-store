/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.drm.db.entity;

import com.junbo.drm.db.Entity;

import javax.persistence.Table;
/**
 * drm.
 */
@javax.persistence.Entity
@Table(name = "DRM")
public class DrmEntity extends Entity {
    @Override
    public Long getId() {
        return null;
    }

    @Override
    public void setId(Long id) {

    }
}
