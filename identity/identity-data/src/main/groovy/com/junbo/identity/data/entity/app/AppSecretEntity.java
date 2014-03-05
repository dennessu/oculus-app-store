/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.entity.app;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by liangfu on 2/19/14.
 */
@Entity
@Table(name = "app_secrect")
public class AppSecretEntity {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "app_id")
    private Long appId;

    @Column(name = "value")
    private String value;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expires_by")
    private Date expiredBy;

    @Column(name = "status")
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getExpiredBy() {
        return expiredBy;
    }

    public void setExpiredBy(Date expiredBy) {
        this.expiredBy = expiredBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
