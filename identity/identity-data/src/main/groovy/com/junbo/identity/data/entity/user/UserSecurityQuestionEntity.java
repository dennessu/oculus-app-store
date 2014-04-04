/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.entity.user;

import com.junbo.identity.data.entity.common.ResourceMetaEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by liangfu on 3/17/14.
 */
@Entity
@Table(name = "user_security_question")
public class UserSecurityQuestionEntity extends ResourceMetaEntity {
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getSecurityQuestionId() {
        return securityQuestionId;
    }

    public void setSecurityQuestionId(Long securityQuestionId) {
        this.securityQuestionId = securityQuestionId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getAnswerSalt() {
        return answerSalt;
    }

    public void setAnswerSalt(String answerSalt) {
        this.answerSalt = answerSalt;
    }

    public String getAnswerHash() {
        return answerHash;
    }

    public void setAnswerHash(String answerHash) {
        this.answerHash = answerHash;
    }

    @Id
    @Column(name = "id")
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "security_question_id")
    private Long securityQuestionId;
    @Column(name = "is_active")
    private Boolean active;
    @Column(name = "answer_salt")
    private String answerSalt;
    @Column(name = "answer_hash")
    private String answerHash;
}
