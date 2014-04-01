/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.entity.domaindata

import com.junbo.identity.data.entity.common.ResourceMetaEntity
import groovy.transform.CompileStatic

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * Created by liangfu on 3/16/14.
 */
@Entity
@Table(name = 'security_question')
@CompileStatic
class SecurityQuestionEntity extends ResourceMetaEntity {
    @Id
    @Column(name = 'id')
    private Long id

    @Column(name = 'value')
    private String value

    @Column(name = 'active')
    private Boolean active

    Long getId() {
        return id
    }

    void setId(Long id) {
        this.id = id
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
