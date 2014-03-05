package com.junbo.drm.db.entity;

import com.junbo.drm.db.Entity;

import javax.persistence.Table;

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
