/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.cloudant.json.serializer;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.impl.BeanAsArraySerializer;
import com.fasterxml.jackson.databind.ser.impl.ObjectIdWriter;
import com.fasterxml.jackson.databind.ser.impl.UnwrappingBeanSerializer;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.junbo.common.cloudant.CloudantEntity;

import java.io.IOException;

/**
 * CustomBeanSerializer.
 */
public class CloudantBeanSerializer extends BeanSerializerBase {

    public CloudantBeanSerializer(BeanSerializerBase source) {
        super(source);
    }

    private CloudantBeanSerializer(CloudantBeanSerializer source,
                                   ObjectIdWriter objectIdWriter) {
        super(source, objectIdWriter);
    }

    private CloudantBeanSerializer(CloudantBeanSerializer source,
                                   String[] toIgnore) {
        super(source, toIgnore);
    }

    private CloudantBeanSerializer(BeanSerializerBase src, ObjectIdWriter objectIdWriter, Object filterId) {
        super(src, objectIdWriter, filterId);
    }

    @Override
    public JsonSerializer<Object> unwrappingSerializer(NameTransformer unwrapper) {
        return new UnwrappingBeanSerializer(this, unwrapper);
    }

    @Override
    public BeanSerializerBase withObjectIdWriter(
            ObjectIdWriter objectIdWriter) {
        return new CloudantBeanSerializer(this, objectIdWriter);
    }


    @Override
    protected BeanSerializerBase withFilterId(Object filterId) {
        return new CloudantBeanSerializer(this, _objectIdWriter, filterId);
    }

    @Override
    protected BeanSerializerBase withIgnorals(String[] toIgnore) {
        return new CloudantBeanSerializer(this, toIgnore);
    }

    @Override
    protected BeanSerializerBase asArraySerializer() {
        /* Can not:
         *
         * - have Object Id (may be allowed in future)
         * - have any getter
         *
         */
        if ((_objectIdWriter == null)
                && (_anyGetterWriter == null)
                && (_propertyFilterId == null)
                ) {
            return new BeanAsArraySerializer(this);
        }
        // already is one, so:
        return this;
    }

    @Override
    public void serialize(Object bean, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonGenerationException {
        updateCloudantId(bean);

        if (_objectIdWriter != null) {
            _serializeWithObjectId(bean, jgen, provider, true);
            return;
        }
        jgen.writeStartObject();
        if (_propertyFilterId != null) {
            serializeFieldsFiltered(bean, jgen, provider);
        } else {
            serializeFields(bean, jgen, provider);
        }
        jgen.writeEndObject();
    }

    @Override
    public void serializeWithType(Object bean, JsonGenerator jgen,
                                  SerializerProvider provider, TypeSerializer typeSer)
            throws IOException, JsonGenerationException {
        updateCloudantId(bean);
        super.serializeWithType(bean, jgen, provider, typeSer);
    }

    private void updateCloudantId(Object bean) {
        if (bean instanceof CloudantEntity) {
            CloudantEntity entity = (CloudantEntity)bean;
            if (entity.getId() != null) {
                entity.setCloudantId(entity.getId().toString());
            } else {
                entity.setCloudantId(null);
            }
        }
    }

    @Override
    public String toString() {
        return "BeanSerializer for " + handledType().getName();
    }
}
