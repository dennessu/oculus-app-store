/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.db.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import javax.persistence.Transient

/**
 * Entity of EmailTemplate
 */
@Entity
@Table(name = 'email_template')
class EmailTemplateEntity extends BaseEntity {
    @Id
    @Column(name = 'id')
    private Long id

    @Column(name='name')
    private String name

    @Column(name='provider_name')
    private String providerName

    @Column(name='provider_index')
    private String providerIndex

    @Column(name='list_of_variables')
    private String vars

    @Column(name='subject')
    private String subject

    @Column(name='from_address')
    private String fromAddress

    @Column(name='from_name')
    private String fromName

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

    String getProviderName() {
        return providerName
    }

    void setProviderName(String providerName) {
        this.providerName = providerName
    }

    String getProviderIndex() {
        return providerIndex
    }

    void setProviderIndex(String providerIndex) {
        this.providerIndex = providerIndex
    }

    String getVars() {
        return vars
    }

    void setVars(String vars) {
        this.vars = vars
    }

    String getSubject() {
        return subject
    }

    void setSubject(String subject) {
        this.subject = subject
    }

    String getFromAddress() {
        return fromAddress
    }

    void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress
    }

    String getFromName() {
        return fromName
    }

    void setFromName(String fromName) {
        this.fromName = fromName
    }

    @Transient
    private List<String> varList

    List<String> getVarList() {
        return varList
    }

    void setVarList(List<String> varList) {
        this.varList = varList
    }
}
