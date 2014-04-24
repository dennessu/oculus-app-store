/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.jackson.serializer;

/**
 * CascadeResource.
 *
 * @deprecated replaced by CompoundAware
 */
@Deprecated
public class CascadeResource {
    private Object primaryId;
    private Object[] cascadeIds;

    public CascadeResource(Object primaryId, Object... cascadeIds) {
        this.primaryId = primaryId;
        this.cascadeIds = cascadeIds;
    }

    public Object[] getCascadeIds() {
        return cascadeIds;
    }

    public Object getPrimaryId() {
        return primaryId;
    }
}
