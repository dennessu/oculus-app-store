/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.csr.spec.model;

/**
 * Created by haomin on 7/16/14.
 */
public class CsrInvitationRequest {
    private String email;
    private String tierGroupId;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTierGroupId() {
        return tierGroupId;
    }

    public void setTierGroupId(String tierGroupId) {
        this.tierGroupId = tierGroupId;
    }
}
