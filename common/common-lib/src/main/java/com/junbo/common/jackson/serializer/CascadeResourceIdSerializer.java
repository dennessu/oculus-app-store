/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.jackson.serializer;

import com.junbo.common.jackson.model.ResourceRef;
import junit.framework.Assert;

/**
 * ResourceIdSerializer.
 */
public class CascadeResourceIdSerializer extends ResourceIdSerializer {
    @Override
    protected ResourceRef handleSingle(Object value) {
        Assert.assertTrue(value instanceof CascadeResourceId);

        ResourceRef ref = new ResourceRef();
        ref.setHref(getResourceHref(value));
        ref.setId(encode(((CascadeResourceId) value).getPrimaryId()));

        return ref;
    }

    @Override
    protected String getResourceHref(Object value) {
        Assert.assertTrue(value instanceof CascadeResourceId);
        CascadeResourceId cascadeResourceId = (CascadeResourceId) value;

        for (int i = 0; i < cascadeResourceId.getCascadeIds().length; i++) {
            cascadeResourceId.getCascadeIds()[i] = encode(cascadeResourceId.getCascadeIds()[i]);
        }

        return RESOURCE_URL_PREFIX + String.format(resourceType, cascadeResourceId.getCascadeIds());
    }
}
