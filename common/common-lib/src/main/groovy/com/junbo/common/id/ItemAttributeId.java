/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * AttributeId.
 */
@IdResourcePath(value = "/item-attributes/{0}", regex = "/item-attributes/(?<id>[0-9A-Z]+)")
public class ItemAttributeId extends Id {

    public ItemAttributeId() {}
    public ItemAttributeId(long value) {
        super(value);
    }
}
