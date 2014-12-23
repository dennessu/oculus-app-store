/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * AttributeId.
 */
@IdResourcePath(value = "/item-attributes/{0}",
                resourceType = "item-attributes",
                regex = "/item-attributes/(?<id>[0-9A-Za-z]+)")
public class ItemAttributeId extends CloudantId {

    public ItemAttributeId() {}
    public ItemAttributeId(String value) {
        super(value);
    }
}
