/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * RoleAssignmentId.
 */
@IdResourcePath(value = "/role-assignments/{0}",
                resourceType = "role-assignments",
                regex = "/role-assignments/(?<id>[0-9A-Za-z]+)")
public class RoleAssignmentId extends CloudantId {
    public RoleAssignmentId() {

    }

    public RoleAssignmentId(String value) {
        super(value);
    }
}
