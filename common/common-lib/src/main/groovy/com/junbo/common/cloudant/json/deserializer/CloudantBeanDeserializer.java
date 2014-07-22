/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.cloudant.json.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializer;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.junbo.common.cloudant.CloudantEntity;

import java.io.IOException;
import java.util.HashSet;

/**
 * CloudantBeanDeserializer.
 */
public class CloudantBeanDeserializer extends BeanDeserializer {

    public CloudantBeanDeserializer(BeanDeserializer source) {
        super(source);
    }

    private CloudantBeanDeserializer(BeanDeserializer source, NameTransformer unwrapper) {
        super(source, unwrapper);
    }

    private CloudantBeanDeserializer(BeanDeserializer source, ObjectIdReader oir) {
        super(source, oir);
    }

    private CloudantBeanDeserializer(BeanDeserializer source, HashSet<String> ignorableProps) {
        super(source, ignorableProps);
    }

    @Override
    public JsonDeserializer<Object> unwrappingDeserializer(NameTransformer unwrapper) {
        return new CloudantBeanDeserializer(this, unwrapper);
    }

    @Override
    public CloudantBeanDeserializer withObjectIdReader(ObjectIdReader oir) {
        return new CloudantBeanDeserializer(this, oir);
    }

    @Override
    public CloudantBeanDeserializer withIgnorableProperties(HashSet<String> ignorableProps) {
        return new CloudantBeanDeserializer(this, ignorableProps);
    }

    @Override
    public Object deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        Object bean = super.deserialize(jp, ctxt);
        if (bean instanceof CloudantEntity) {
            CloudantEntity entity = (CloudantEntity)bean;

            // force update id by calling the setter.
            entity.setCloudantId(entity.getCloudantId());
        }
        return bean;
    }
}
