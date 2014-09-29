/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.BeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;

/**
 * PropertyAssignedAwareFilter class.
 */
public class PropertyAssignedAwareFilter implements BeanPropertyFilter {

    @Override
    public void serializeAsField(
            Object bean,
            JsonGenerator jgen,
            SerializerProvider prov,
            BeanPropertyWriter writer)
            throws Exception {

        if (bean instanceof PropertyAssignedAware) {
            PropertyAssignedAware propertyAssignedAware = (PropertyAssignedAware) bean;

            // skip if not assigned.
            if (!propertyAssignedAware.isPropertyAssigned(writer.getName())) {
                return;
            }

            Object value = writer.get(bean);

            // always write explicit null
            if (value == null) {
                jgen.writeFieldName(writer.getName());
                jgen.writeNull();
                return;
            }
        }

        writer.serializeAsField(bean, jgen, prov);
    }

    @Override
    public void depositSchemaProperty(
            BeanPropertyWriter writer,
            ObjectNode propertiesNode,
            SerializerProvider provider)
            throws JsonMappingException {

        writer.depositSchemaProperty(propertiesNode, provider);
    }

    @Override
    public void depositSchemaProperty(
            BeanPropertyWriter writer,
            JsonObjectFormatVisitor objectVisitor,
            SerializerProvider provider)
            throws JsonMappingException {

        writer.depositSchemaProperty(objectVisitor);
    }
}
