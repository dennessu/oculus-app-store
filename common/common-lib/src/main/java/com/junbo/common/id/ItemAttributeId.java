/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * AttributeId.
 */
@IdResourcePath("/item-attributes/{0}")
public class ItemAttributeId extends Id {

    public ItemAttributeId() {}
    public ItemAttributeId(long value) {
        super(value);
    }
}
