/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.entity.reverselookup

import com.junbo.sharding.annotations.SeedId

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * Created by liangfu on 3/21/14.
 */
@Entity
@Table(name = 'group_name_reverse_index')
class GroupReverseIndexEntity {
    @Id
    @SeedId
    @Column(name = 'value')
    private String name

    @Column(name = 'group_id')
    private Long groupId

    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }

    Long getGroupId() {
        return groupId
    }

    void setGroupId(Long groupId) {
        this.groupId = groupId
    }
}
