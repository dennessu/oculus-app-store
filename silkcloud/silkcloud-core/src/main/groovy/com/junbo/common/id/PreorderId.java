/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.id;

/**
 * Java doc for PreorderId.
 */
@IdResourcePath(value = "/pre-orders/{0}",
                resourceType = "pre-orders",
                regex = "/pre-orders/(?<id>[0-9A-Za-z]+)")
public class PreorderId extends Id {
    public PreorderId() {}

    public PreorderId(Long value) {
        super(value);
    }
}
