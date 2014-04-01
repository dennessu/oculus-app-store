/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.entity.common

import groovy.transform.CompileStatic

import javax.persistence.*
/**
 * Created by liangfu on 3/14/14.
 */
@MappedSuperclass
@CompileStatic
abstract class ResourceMetaEntity {
    @Column(name = 'version')
    @Version
    private Integer resourceAge

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = 'created_time')
    private Date createdTime

    @Column(name = 'created_by')
    private String createdBy

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = 'modified_time')
    private Date updatedTime

    @Column(name = 'modified_by')
    private String updatedBy

    Integer getResourceAge() {
        return resourceAge
    }

    void setResourceAge(Integer resourceAge) {
        this.resourceAge = resourceAge
    }

    Date getCreatedTime() {
        return createdTime
    }

    void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime
    }

    String getCreatedBy() {
        return createdBy
    }

    void setCreatedBy(String createdBy) {
        this.createdBy = createdBy
    }

    Date getUpdatedTime() {
        return updatedTime
    }

    void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime
    }

    String getUpdatedBy() {
        return updatedBy
    }

    void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy
    }

}
