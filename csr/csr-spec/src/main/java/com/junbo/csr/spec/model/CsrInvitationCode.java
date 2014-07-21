/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.csr.spec.model;

import com.junbo.common.id.GroupId;
import com.junbo.common.id.UserGroupId;
import com.junbo.common.id.UserId;
import com.junbo.common.model.ResourceMeta;

/**
 * Created by haomin on 7/16/14.
 */
public class CsrInvitationCode extends ResourceMeta<String> {
    private String code;
    private String email;
    private UserId userId;
    private GroupId pendingGroupId;
    private GroupId inviteGroupId;
    private UserGroupId userGroupId;

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

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public GroupId getPendingGroupId() {
        return pendingGroupId;
    }

    public void setPendingGroupId(GroupId pendingGroupId) {
        this.pendingGroupId = pendingGroupId;
    }

    public GroupId getInviteGroupId() {
        return inviteGroupId;
    }

    public void setInviteGroupId(GroupId inviteGroupId) {
        this.inviteGroupId = inviteGroupId;
    }

    public UserGroupId getUserGroupId() {
        return userGroupId;
    }

    public void setUserGroupId(UserGroupId userGroupId) {
        this.userGroupId = userGroupId;
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
