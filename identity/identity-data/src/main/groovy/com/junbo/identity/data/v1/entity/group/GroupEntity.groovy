/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.identity.data.v1.entity.group

import com.junbo.identity.data.v1.entity.common.ResourceMetaEntity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
/**
 * Created by liangfu on 3/14/14.
 */
@Entity
@Table(name = 'group')
class GroupEntity extends ResourceMetaEntity {
    @Id
    @Column(name = 'id')
    private Long groupId

    @Column(name = 'value')
    private String value

    @Column(name = 'active', nullable = false)
    private Boolean active

    Long getGroupId() {
        return groupId
    }

    void setGroupId(Long groupId) {
        this.groupId = groupId
    }

    String getValue() {
        return value
    }

    void setValue(String value) {
        this.value = value
    }

    Boolean getActive() {
        return active
    }

    void setActive(Boolean active) {
        this.active = active
    }
}
