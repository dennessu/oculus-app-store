/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.junbo.common.jackson.annotation.ResourcePath;
import com.junbo.common.jackson.aware.AnnotationsAware;
import com.junbo.common.jackson.model.ResourceRef;
import com.junbo.common.json.ObjectMapperProvider;
import com.junbo.common.shuffle.Oculus48Id;
import com.junbo.common.util.Utils;
import com.junbo.configuration.ConfigService;
import com.junbo.configuration.ConfigServiceManager;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * ResourceIdSerializer.
 */
public class ResourceIdSerializer extends JsonSerializer<Object> implements AnnotationsAware {

    private ObjectMapper mapper = ObjectMapperProvider.instance();

    protected String resourceUrlPrefix = "https://api.oculus.com/v1";

    protected String resourcePath;

    public ResourceIdSerializer() {
        ConfigService configService = ConfigServiceManager.instance();
        if (configService != null) {
            String prefixFromConfig = configService.getConfigValue("common.conf.resourceUrlPrefix");
            if (prefixFromConfig != null) {
                resourceUrlPrefix = prefixFromConfig;
            }
        }
    }

    @Override
    public void injectAnnotations(Annotated annotations) {
        ResourcePath typeAnno = annotations.getAnnotation(ResourcePath.class);
        if (typeAnno == null) {
            throw new IllegalStateException("ResourcePath annotation is missing.");
        }
        this.resourcePath = typeAnno.value();
    }

    @Override
    public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException {
        Assert.notNull(resourcePath);

        if (unwrap(value) == null) {
            mapper.writeValue(jgen, null);
            return;
        }

        Object results = isCollection(value) ? handleCollection(value) : handleSingle(value);
        mapper.writeValue(jgen, results);
    }

    protected Object unwrap(Object value) {
        return value;
    }

    protected String encode(Object value) {
        if (value instanceof Long) {
            Oculus48Id.validateRawValue((Long) value);
            return Oculus48Id.format(Oculus48Id.shuffle((Long) value));
        } else {
            return value == null ? "" : value.toString();
        }
    }

    protected String getResourceHref(Object value) {
        return Utils.combineUrl(resourceUrlPrefix, resourcePath, encode(value));
    }

    protected List<ResourceRef> handleCollection(Object value) {
        Collection collection = (Collection) value;

        List<ResourceRef> results = new ArrayList<>();
        Iterator it = collection.iterator();
        while (it.hasNext()) {
            results.add(handleSingle(it.next()));
        }

        return results;
    }

    protected ResourceRef handleSingle(Object value) {
        ResourceRef ref = new ResourceRef();
        ref.setHref(getResourceHref(value));
        ref.setId(encode(value));

        return ref;
    }

    private boolean isCollection(Object value) {
        return value instanceof Collection;
    }
}
