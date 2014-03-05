/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.model.app;

import java.util.Date;
/**
 * Java cod for AppSecret.
 */

public class AppSecret {
    private String value;
    private Date expiredBy;
    private String status;

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
