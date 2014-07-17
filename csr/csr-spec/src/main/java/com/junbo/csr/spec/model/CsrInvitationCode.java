/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.csr.spec.model;

import com.junbo.common.model.ResourceMeta;

/**
 * Created by haomin on 7/16/14.
 */
public class CsrInvitationCode extends ResourceMeta<String> {
    private String code;
    private String email;
    private Long userId;
    private String pendingGroupId;
    private String inviteGroupId;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPendingGroupId() {
        return pendingGroupId;
    }

    public void setPendingGroupId(String pendingGroupId) {
        this.pendingGroupId = pendingGroupId;
    }

    public String getInviteGroupId() {
        return inviteGroupId;
    }

    public void setInviteGroupId(String inviteGroupId) {
        this.inviteGroupId = inviteGroupId;
    }

    @Override
    public String getId() {
        return code;
    }

    @Override
    public void setId(String id) {
        this.code = id;
    }
}
