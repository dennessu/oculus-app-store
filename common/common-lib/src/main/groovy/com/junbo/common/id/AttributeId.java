/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * AttributeId.
 */
@IdResourcePath(value = "/attributes/{0}", regex = "/attributes/(?<id>[0-9A-Z]+)")
public class AttributeId extends Id {

    public AttributeId() {}
    public AttributeId(long value) {
        super(value);
    }
}
