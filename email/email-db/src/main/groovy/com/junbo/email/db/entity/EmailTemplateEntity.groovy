/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.db.entity

import groovy.transform.CompileStatic

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * Entity of EmailTemplate
 */
@CompileStatic
@Entity
@Table(name = 'email_template')
class EmailTemplateEntity extends BaseEntity {
    @Id
    @Column(name = 'id')
    private Long id

    @Column(name='name')
    private String name

    @Column(name='source')
    private String source

    @Column(name='action')
    private String action

    @Column(name='locale')
    private String locale

    @Column(name='provider_name')
    private String providerName

    @Column(name='provider_index')
    private String providerIndex

    @Column(name='placeholder_names')
    private String placeholderNames

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

    String getSource() {
        return source
    }

    void setSource(String source) {
        this.source = source
    }

    String getAction() {
        return action
    }

    void setAction(String action) {
        this.action = action
    }

    String getLocale() {
        return locale
    }

    void setLocale(String locale) {
        this.locale = locale
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

    String getPlaceholderNames() {
        return placeholderNames
    }

    void setPlaceholderNames(String placeholderNames) {
        this.placeholderNames = placeholderNames
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
}
