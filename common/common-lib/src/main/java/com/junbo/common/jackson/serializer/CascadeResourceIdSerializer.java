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
        Assert.assertTrue(value instanceof CascadeResource);

        ResourceRef ref = new ResourceRef();
        ref.setHref(getResourceHref(value));
        ref.setId(encode(((CascadeResource) value).getPrimaryId()));

        return ref;
    }

    @Override
    protected String getResourceHref(Object value) {
        Assert.assertTrue(value instanceof CascadeResource);
        CascadeResource resource = (CascadeResource) value;

        Object[] encodedIds = new String[resource.getCascadeIds().length];
        for (int i = 0; i < resource.getCascadeIds().length; i++) {
            encodedIds[i] = encode(resource.getCascadeIds()[i]);
        }

        return RESOURCE_URL_PREFIX + String.format(resourceType, encodedIds);
    }
}
