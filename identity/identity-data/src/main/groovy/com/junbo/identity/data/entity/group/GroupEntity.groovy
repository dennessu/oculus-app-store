/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.identity.data.entity.group

import com.junbo.identity.data.entity.common.ResourceMetaEntity
import com.junbo.sharding.annotations.SeedId

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
/**
 * Created by liangfu on 3/14/14.
 */
@Entity
@Table(name = 'group_entity')
class GroupEntity extends ResourceMetaEntity {
    @Id
    @SeedId
    @Column(name = 'id')
    private Long id

    @Column(name = 'value')
    private String name

    @Column(name = 'active', nullable = false)
    private Boolean active

    Long getId() {
        return id
    }

    void setId(Long id) {
        this.id = id
    }

    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }

    Boolean getActive() {
        return active
    }

    void setActive(Boolean active) {
        this.active = active
    }
}
