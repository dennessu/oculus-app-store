/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * Created by minhao on 2/13/14.
 */
@IdResourcePath(value = "/organizations/{0}",
                resourceType = "organizations",
                regex = "/organizations/(?<id>[0-9A-Za-z]+)")
public class OrganizationId extends Id {

    public OrganizationId() {}
    public OrganizationId(Long value) {
        super(value);
    }
}
