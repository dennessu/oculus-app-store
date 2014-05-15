/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * RoleId.
 */
@IdResourcePath(value = "/roles/{0}", regex = "/roles/(?<id>[0-9A-Z]+)")
public class RoleId extends Id {
    public RoleId() {

    }

    public RoleId(Long value) {
        super(value);
    }
}
