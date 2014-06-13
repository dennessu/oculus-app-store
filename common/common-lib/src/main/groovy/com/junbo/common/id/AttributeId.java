/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * AttributeId.
 */
@IdResourcePath(value = "/attributes/{0}",
                resourceType = "attributes",
                regex = "/attributes/(?<id>[0-9A-Za-z]+)")
public class AttributeId extends CloudantId {

    public AttributeId() {}
    public AttributeId(String value) {
        super(value);
    }
}
