/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * RoleId.
 */
@IdResourcePath(value = "/roles/{0}",
                resourceType = "roles",
                regex = "/roles/(?<id>[0-9A-Za-z]+)")
public class RoleId extends CloudantId {
    public RoleId() {

    }

    public RoleId(String value) {
        super(value);
    }
}
