/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.authorization.spec.model.Role;
import com.junbo.authorization.spec.model.RoleTarget;
import com.junbo.common.id.RoleId;
import com.junbo.common.model.Link;
import com.junbo.identity.spec.v1.model.User;
import com.junbo.test.common.RandomHelper;

/**
 * @author dw
 */
public class AuthorizationModel {

    private AuthorizationModel() {

    }

    public static Role DefaultRole() throws Exception {
        RoleId id = new RoleId();
        id.setValue(String.valueOf(RandomHelper.randomLong()));
        RoleTarget target = new RoleTarget();
        target.setTargetType("default-target-type");
        target.setFilterType("default-filter-type");
        Link link = new Link();
        User user = Identity.UserPostDefault();
        link.setId(Identity.GetHexLongId(user.getId().getValue()));
        link.setHref("/v1/users/" + Identity.GetHexLongId(user.getId().getValue()));
        target.setFilterLink(link);
        return DefaultRole(id, target);
    }

    public static Role DefaultRole(RoleId roleId, RoleTarget target) throws Exception {
        Role role = new Role();
        role.setId(roleId);
        role.setName(RandomHelper.randomName());
        role.setTarget(target);
        return role;
    }
}
