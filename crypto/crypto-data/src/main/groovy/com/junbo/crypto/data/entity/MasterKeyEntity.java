/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.crypto.data.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by liangfu on 5/12/14.
 */
@Entity
@Table(name = "master_key")
public class MasterKeyEntity {
    @Id
    @Column(name = "key_version")
    private Long keyVersion;

    @Column(name = "version")
    @Version
    private Integer resourceAge;

    @Column(name = "value")
    // It must be versionNumber:encryptValue
    private String encryptValue;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_time")
    private Date createdTime;

    public Integer getResourceAge() {
        return resourceAge;
    }

    public void setResourceAge(Integer resourceAge) {
        this.resourceAge = resourceAge;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public String getEncryptValue() {
        return encryptValue;
    }

    public void setEncryptValue(String encryptValue) {
        this.encryptValue = encryptValue;
    }

    public Long getKeyVersion() {
        return keyVersion;
    }

    public void setKeyVersion(Long keyVersion) {
        this.keyVersion = keyVersion;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
