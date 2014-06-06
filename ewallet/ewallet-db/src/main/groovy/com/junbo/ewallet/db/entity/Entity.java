/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.db.entity;

import com.junbo.common.cloudant.json.annotations.CloudantDeserialize;
import com.junbo.common.cloudant.json.annotations.CloudantSerialize;
import com.junbo.ewallet.db.entity.def.DateDeserializer;
import com.junbo.ewallet.db.entity.def.DateSerializer;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Date;

/**
 * Base entity.
 */
@MappedSuperclass
public abstract class Entity extends EntityWithCreated {
    private Long updatedBy;
    @CloudantSerialize(DateSerializer.class)
    @CloudantDeserialize(DateDeserializer.class)
    private Date updatedTime;

    @Column(name = "modified_by")
    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Column(name = "modified_time")
    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }
}
