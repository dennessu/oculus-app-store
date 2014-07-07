/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.db.entity;

import com.junbo.token.spec.enums.SetStatus;
import com.junbo.token.spec.enums.TokenLength;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Token Set Entity.
 */
@Entity
@Table(name = "token_set")
public class TokenSetEntity extends GenericEntity {
    @Id
    @Column(name = "token_set_id")
    private String id;

    @Column(name = "description")
    private String description;

    @Column(name = "token_set_status")
    private SetStatus status;

    @Column(name = "generation_length")
    private TokenLength generationLength;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SetStatus getStatus() {
        return status;
    }

    public void setStatus(SetStatus status) {
        this.status = status;
    }

    public TokenLength getGenerationLength() {
        return generationLength;
    }

    public void setGenerationLength(TokenLength generationLength) {
        this.generationLength = generationLength;
    }

}
