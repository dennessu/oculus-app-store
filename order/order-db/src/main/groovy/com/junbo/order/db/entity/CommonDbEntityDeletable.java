/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.db.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * Created by fzhang on 14-3-11.
 */
@MappedSuperclass
public abstract class CommonDbEntityDeletable extends CommonDbEntityWithDate {

    private boolean deleted;

    @Column(name = "IS_DELETED")
    public Boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
