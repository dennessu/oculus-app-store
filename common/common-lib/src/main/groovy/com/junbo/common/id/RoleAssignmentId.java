/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * RoleAssignmentId.
 */
@IdResourcePath("/role-assignments/{0}")
public class RoleAssignmentId extends Id {
    public RoleAssignmentId() {

    }

    public RoleAssignmentId(Long value) {
        super(value);
    }
}
