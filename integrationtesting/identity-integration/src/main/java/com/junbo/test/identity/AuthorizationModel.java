/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;
// CHECKSTYLE:OFF
import com.junbo.authorization.spec.model.Role;
import com.junbo.authorization.spec.model.RoleTarget;
import com.junbo.common.id.RoleId;
import com.junbo.common.model.Link;
import com.junbo.identity.spec.v1.model.Organization;
import com.junbo.test.common.RandomHelper;

import java.util.ArrayList;
import java.util.List;

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
        target.setTargetType(RandomRoleTargetType());
        target.setFilterType(RandomRoleFilterType());
        Link link = new Link();
        Organization organization = Identity.OrganizationPostDefault(null);
        link.setId(Identity.GetHexLongId(organization.getId().getValue()));
        link.setHref("/v1/organizations/" + Identity.GetHexLongId(organization.getId().getValue()));
        target.setFilterLink(link);
        return DefaultRole(id, target);
    }

    public static Role DefaultRole(RoleId roleId, RoleTarget target) throws Exception {
        Role role = new Role();
        role.setId(roleId);
        role.setName(RandomRoleName());
        role.setTarget(target);
        return role;
    }

    public static String RandomRoleName() {
        List<Object> names = new ArrayList<>();
        for (Object obj : AllowedRoleName.values()) {
            names.add(obj.toString());
        }
        return RandomHelper.randomValueFromList(names).toString();
    }

    public static String RandomRoleTargetType() {
        List<Object> targetTypes = new ArrayList<>();
        for (Object obj : AllowedRoleTargetType.values()) {
            targetTypes.add(obj.toString());
        }
        return RandomHelper.randomValueFromList(targetTypes).toString();
    }

    public static String RandomRoleFilterType() {
        List<Object> filterTypes = new ArrayList<>();
        for (Object obj : AllowedRoleFilterType.values()) {
            filterTypes.add(obj.toString());
        }
        return RandomHelper.randomValueFromList(filterTypes).toString();
    }

    /**
     * role name.
     */
    public enum AllowedRoleName {
        Admin,
        Developer,
        Publisher
    }

    /**
     * target type.
     */
    public enum AllowedRoleTargetType {
        organizations
    }

    /**
     * fields type.
     */
    public enum AllowedRoleFilterType {
        SINGLEINSTANCEFILTER
    }
}
