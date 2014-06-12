/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Created by liangfu on 4/17/14.
 */
@IdResourcePath(value = "/optin-type/{0}",
                resourceType = "optin-type",
                regex = "/optin-type/(?<id>[0-9A-Za-z]+)")
public class OptinTypeId extends CloudantId {

    public OptinTypeId() {}

    public OptinTypeId(String value) {
        super(value);
    }
}
