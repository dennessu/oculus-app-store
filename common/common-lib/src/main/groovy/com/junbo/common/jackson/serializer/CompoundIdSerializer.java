/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.jackson.serializer;

import com.junbo.common.filter.OverrideApiHostFilter;
import com.junbo.common.jackson.aware.CompoundAware;
import com.junbo.common.jackson.model.ResourceRef;
import com.junbo.common.util.Context;
import com.junbo.common.util.Utils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;

/**
 * CompoundIdSerializer.
 */
public class CompoundIdSerializer extends ResourceIdSerializer {
    @Override
    protected ResourceRef handleSingle(Object value) {
        Assert.isTrue(value instanceof CompoundAware);

        Object primaryId = ((CompoundAware) value).getPrimaryId();
        ResourceRef ref = new ResourceRef();
        ref.setHref(getResourceHref(value));
        ref.setId(encode(primaryId));

        return ref;
    }

    @Override
    protected String getResourceHref(Object value) {
        Assert.isTrue(value instanceof CompoundAware);

        String path = resourcePath;
        try {
            for (Field field : value.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object fieldValue = field.get(value);
                path = path.replace("{" + field.getName() + "}", encode(fieldValue));
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error occurred during serializing CompoundId.");
        }

        String urlPrefix = resourceUrlPrefix;
        String apiHost = Context.get().getHeader(OverrideApiHostFilter.X_OVERRIDE_API_HOST);

        if (StringUtils.hasText(apiHost)) {
            urlPrefix = apiHost;
        }

        return Utils.combineUrl(urlPrefix, path);
    }
}
