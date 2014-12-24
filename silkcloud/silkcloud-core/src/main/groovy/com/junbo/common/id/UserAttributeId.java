/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Created by xiali_000 on 2014/12/19.
 */
@IdResourcePath(value = "/user-attributes/{0}",
        resourceType = "user-attributes",
        regex = "/user-attributes/(?<id>[0-9A-Za-z]+)")
public class UserAttributeId extends CloudantId {
    public UserAttributeId() {}
    public UserAttributeId(String value) {
        super(value);
    }
}
