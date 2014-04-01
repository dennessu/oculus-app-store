/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.entity.user

import com.junbo.identity.data.entity.common.ResourceMetaEntity
import com.junbo.sharding.annotations.SeedId
import groovy.transform.CompileStatic

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * Created by liangfu on 3/17/14.
 */
@Entity
@Table(name = 'user_security_question')
@CompileStatic
class UserSecurityQuestionEntity extends ResourceMetaEntity {
    @Id
    @Column(name = 'id')
    private Long id

    @SeedId
    @Column(name = 'user_id')
    private Long userId

    @Column(name = 'security_question_id')
    private Long securityQuestionId

    @Column(name = 'is_active')
    private Boolean active

    @Column(name = 'answer_salt')
    private String answerSalt

    @Column(name = 'answer_hash')
    private String answerHash

    Long getId() {
        return id
    }

    void setId(Long id) {
        this.id = id
    }

    Long getUserId() {
        return userId
    }

    void setUserId(Long userId) {
        this.userId = userId
    }

    Long getSecurityQuestionId() {
        return securityQuestionId
    }

    void setSecurityQuestionId(Long securityQuestionId) {
        this.securityQuestionId = securityQuestionId
    }

    Boolean getActive() {
        return active
    }

    void setActive(Boolean active) {
        this.active = active
    }

    String getAnswerSalt() {
        return answerSalt
    }

    void setAnswerSalt(String answerSalt) {
        this.answerSalt = answerSalt
    }

    String getAnswerHash() {
        return answerHash
    }

    void setAnswerHash(String answerHash) {
        this.answerHash = answerHash
    }
}
