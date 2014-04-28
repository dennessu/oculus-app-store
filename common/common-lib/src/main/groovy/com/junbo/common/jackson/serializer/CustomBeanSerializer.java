/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.impl.BeanAsArraySerializer;
import com.fasterxml.jackson.databind.ser.impl.ObjectIdWriter;
import com.fasterxml.jackson.databind.ser.impl.UnwrappingBeanSerializer;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.junbo.common.id.Id;
import com.junbo.common.jackson.annotation.HateoasLink;
import com.junbo.common.model.Link;
import com.junbo.common.shuffle.Oculus40Id;
import com.junbo.common.shuffle.Oculus48Id;
import com.junbo.common.util.IdFormatter;
import com.junbo.common.util.Utils;
import com.junbo.configuration.ConfigService;
import com.junbo.configuration.ConfigServiceManager;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CustomBeanSerializer.
 */
public class CustomBeanSerializer extends BeanSerializerBase {

    public CustomBeanSerializer(BeanSerializerBase source) {
        super(source);
        parseHateoasLinkFields();
    }

    private CustomBeanSerializer(CustomBeanSerializer source,
                                 ObjectIdWriter objectIdWriter) {
        super(source, objectIdWriter);
        parseHateoasLinkFields();
    }

    private CustomBeanSerializer(CustomBeanSerializer source,
                                 String[] toIgnore) {
        super(source, toIgnore);
        parseHateoasLinkFields();
    }

    @Override
    public JsonSerializer<Object> unwrappingSerializer(NameTransformer unwrapper) {
        return new UnwrappingBeanSerializer(this, unwrapper);
    }

    @Override
    public BeanSerializerBase withObjectIdWriter(
            ObjectIdWriter objectIdWriter) {
        return new CustomBeanSerializer(this, objectIdWriter);
    }

    @Override
    protected BeanSerializerBase withIgnorals(String[] toIgnore) {
        return new CustomBeanSerializer(this, toIgnore);
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
        fillHateoasLinks(bean);

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

        clearHateoasLinks(bean);
    }

    @Override
    public String toString() {
        return "BeanSerializer for " + handledType().getName();
    }

    ///////////////////////////////////////////////////////////
    // HATEOAS field filling
    ///////////////////////////////////////////////////////////

    private static class HateoasLinkField {
        private static final Pattern FIELD_PATTERN = Pattern.compile("\\{([^}]*)\\}");
        private static String resourceUrlPrefix;

        private static String readResourceUrlPrefix() {
            String result = "https://api.oculusvr.com/v1";
            ConfigService configService = ConfigServiceManager.instance();
            if (configService != null) {
                String prefixFromConfig = configService.getConfigValue("common.conf.resourceUrlPrefix");
                if (prefixFromConfig != null) {
                    result = prefixFromConfig;
                }
            }
            return result;
        }

        public HateoasLinkField(Field field, String template, Map<String, Field> allFields) {
            this.field = field;
            this.template = template;

            if (resourceUrlPrefix == null) {
                resourceUrlPrefix = readResourceUrlPrefix();
            }

            if (!field.getType().equals(Link.class)) {
                throw new RuntimeException(
                        "Hateoas link field " + field.getName() + ": " +
                                "Invalid type, must be " + Link.class.getName()
                );
            }

            try {
                fieldSetter = new PropertyDescriptor(field.getName(), field.getDeclaringClass()).getWriteMethod();
            } catch (IntrospectionException ex) {
                throw new RuntimeException(
                        "Hateoas link field " + field.getName() + ": " +
                                "setter method not found."
                );
            }

            Matcher matcher = FIELD_PATTERN.matcher(template);
            while (matcher.find()) {
                String fieldName = matcher.group(1);

                Field templateField = allFields.get(fieldName);
                if (templateField == null) {
                    throw new RuntimeException("Field not found: " + fieldName + " HATEOAS link template: " + template);
                }

                referencedFields.put(fieldName, new ParamField(templateField));
            }
        }

        public void setValue(Object bean, Map<String, String> refFieldValues) {
            if (bean == null) return;

            boolean hasEmptyValue = false;
            StringBuffer buffer = new StringBuffer();
            Matcher matcher = FIELD_PATTERN.matcher(template);
            while (matcher.find()) {
                String fieldName = matcher.group(1);

                String value = refFieldValues.get(fieldName);
                if (value == null) {
                    // found empty value, mark this hateoas link as invalid
                    hasEmptyValue = true;
                    break;
                }

                matcher.appendReplacement(buffer, value);
            }
            matcher.appendTail(buffer);

            Link link = null;
            if (!hasEmptyValue) {
                link = new Link();
                link.setHref(Utils.combineUrl(resourceUrlPrefix, buffer.toString()));
            }

            try {
                fieldSetter.invoke(bean, link);
            } catch (InvocationTargetException|IllegalAccessException ex) {
                throw new RuntimeException("Error setting hateoas link: " + field.getName(), ex);
            }
        }

        public void clearValue(Object bean) {
            if (bean == null) return;

            try {
                fieldSetter.invoke(bean, (Object)null);
            } catch (InvocationTargetException|IllegalAccessException ex) {
                throw new RuntimeException("Error setting hateoas link: " + field.getName(), ex);
            }
        }

        public Map<String, ParamField> getReferencedFields() {
            return referencedFields;
        }

        private Field field;
        private Method fieldSetter;
        private String template;
        private Map<String, ParamField> referencedFields = new HashMap<>();
    }

    private static class ParamField {
        public ParamField(Field field) {
            this.field = field;
            checkIdAnnotation(field.getAnnotations());
        }

        public String getName() {
            return field.getName();
        }

        public String getValue(Object bean) {
            try {
                Object fieldValue = field.get(bean);
                String result = null;
                if (fieldValue == null) {
                    result = null;
                } else if (fieldValue instanceof Id) {
                    result = IdFormatter.encodeId((Id) fieldValue);
                } else if (hasOrderIdAnnotation) {
                    result = Oculus40Id.format(Oculus40Id.shuffle((Long) fieldValue));
                } else if (hasIdAnnotation) {
                    result = Oculus48Id.format(Oculus48Id.shuffle((Long) fieldValue));
                } else {
                    result = fieldValue.toString();
                }
                return result;
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(
                        "Failed to get value for field: " + field.getName() + " in " + bean.getClass(), ex);
            }
        }

        private void checkIdAnnotation(Annotation[] annotations) {
            if (annotations == null) return;

            for (Annotation annotation : annotations) {
                if (annotation.annotationType().equals(com.junbo.common.jackson.annotation.OrderId.class)) {
                    hasOrderIdAnnotation = true;
                }

                String annotationClassName = annotation.annotationType().getName();
                if (annotationClassName.startsWith("com.junbo.common.jackson.annotation.") &&
                        annotationClassName.endsWith("Id")) {
                    hasIdAnnotation = true;
                }
            }
        }

        private Field field;
        private boolean hasIdAnnotation;
        private boolean hasOrderIdAnnotation;
    }

    private static Map<String, Field> getAllFields(Class cls) {
        Map<String, Field> result = new HashMap<>();

        // doesn't support super class for now.
        for (Field field : cls.getDeclaredFields()) {
            if (!field.isSynthetic()) {
                field.setAccessible(true);
                if (!result.containsKey(field.getName())) {
                    // prevent fields got overridden by private fields from superclass
                    result.put(field.getName(), field);
                }
            }
        }
        return result;
    }

    private List<HateoasLinkField> hateoasLinkFields = new ArrayList<>();
    private Map<String, ParamField> allRefFields = new HashMap<>();

    private void parseHateoasLinkFields() {
        Map<String, Field> fields = getAllFields(handledType());
        for (Field field : fields.values()) {
            HateoasLink link = field.getAnnotation(HateoasLink.class);
            if (link != null) {
                HateoasLinkField linkField = new HateoasLinkField(field, link.value(), fields);
                hateoasLinkFields.add(linkField);
                allRefFields.putAll(linkField.getReferencedFields());
            }
        }
    }


    private void fillHateoasLinks(Object bean) {
        Map<String, String> fieldValues = new HashMap<>();

        for (ParamField field : allRefFields.values()) {
            fieldValues.put(field.getName(), field.getValue(bean));
        }

        for (HateoasLinkField link : hateoasLinkFields) {
            link.setValue(bean, fieldValues);
        }
    }

    private void clearHateoasLinks(Object bean) {
        for (HateoasLinkField link : hateoasLinkFields) {
            link.clearValue(bean);
        }
    }
}
