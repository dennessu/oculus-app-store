/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Created by liangfu on 7/22/14.
 */
@IdResourcePath(value = "/error-info/{0}",
        resourceType = "error-info",
        regex = "/error-info/(?<id>[0-9A-Za-z]+)")
public class ErrorIdentifier extends CloudantId {
    public ErrorIdentifier() {}

    public ErrorIdentifier(String value) {
        super(value);
    }
}
