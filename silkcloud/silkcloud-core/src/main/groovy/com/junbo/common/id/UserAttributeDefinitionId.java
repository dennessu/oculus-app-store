/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Created by xiali_000 on 2014/12/19.
 */
@IdResourcePath(value = "/user-attribute-definitions/{0}",
        resourceType = "user-attribute-definitions",
        regex = "/user-attribute-definitions/(?<id>[0-9A-Za-z]+)")
public class UserAttributeDefinitionId extends CloudantId{
    public UserAttributeDefinitionId() {}
    public UserAttributeDefinitionId(String value) {
        super(value);
    }
}
