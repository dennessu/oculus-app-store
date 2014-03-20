/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.jackson.serializer;

import com.junbo.common.jackson.model.ResourceRef;
import junit.framework.Assert;

/**
 * CompoundIdSerializer.
 */
public class CompoundIdSerializer extends ResourceIdSerializer {
    @Override
    protected ResourceRef handleSingle(Object value) {
        Assert.assertTrue(value instanceof CompoundAware);

        Object primaryId = ((CompoundAware) value).getPrimaryId();
        ResourceRef ref = new ResourceRef();
        ref.setHref(getResourceHref(value));
        ref.setId(encode(primaryId));

        return ref;
    }

    @Override
    protected String getResourceHref(Object value) {
        Assert.assertTrue(value instanceof CompoundAware);

        return "hello";
    }
}
