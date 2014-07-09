/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.junbo.common.error.AppCommonErrors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Common Util.
 */
public final class CommonUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtil.class);
    private static final String SHA_ALG = "SHA-256";
    private static final String ENCODING = "UTF-8";

    private CommonUtil(){

    }

    public static boolean isNullOrEmpty(String value){
        return value == null || value.isEmpty();
    }

    public static String toJson(Object input, String[] filterProperties) {
        SerializeWriter writer = new SerializeWriter();
        JSONSerializer serializer = new JSONSerializer(writer);

        // apply filters
        if (filterProperties != null) {
            final Set<String> filters = new HashSet<String>(Arrays.asList(filterProperties));
            serializer.getPropertyFilters().add(new PropertyFilter() {
                public boolean apply(Object source, String name, Object value) {
                    return !filters.contains(name);
                }
            });
        }

        serializer.write(input);
        return writer.toString();
    }

    public static <T> T parseJson(String json, Class<T> classType){
        return JSON.parseObject(json, classType);
    }

    public static boolean toBool(String value){
        return value.equalsIgnoreCase("Yes") || value.equalsIgnoreCase("True");
    }

    public static void preValidation(Object obj) {
        if(obj instanceof List){
            for(int i = 0; i < ((List) obj).size(); i ++){
                preValidation(((List) obj).get(i));
            }
        }
        for(Field field : obj.getClass().getDeclaredFields()){
            for(Annotation annotation : field.getAnnotations()){
                if(annotation instanceof FilterIn){
                    Object value = null;
                    try{
                        value = new PropertyDescriptor(field.getName(), obj.getClass()).getReadMethod().invoke(obj);
                    }catch (Exception ex){
                        throw AppCommonErrors.INSTANCE.fieldMustBeNull(field.getName()).exception();
                    }
                    if(value != null){
                        throw AppCommonErrors.INSTANCE.fieldMustBeNull(field.getName()).exception();
                    }
                }else if(annotation instanceof InnerFilter){
                    try{
                        Object sub = new PropertyDescriptor(field.getName(),
                                obj.getClass()).getReadMethod().invoke(obj);
                        if(sub != null){
                            preValidation(sub);
                        }
                    }catch (Exception ex){
                        throw AppCommonErrors.INSTANCE.fieldMustBeNull(field.getName()).exception();
                    }
                }
            }
        }
    }

    public static void postFilter(Object obj) {
        if(obj instanceof List){
            for(int i = 0; i < ((List) obj).size(); i ++){
                postFilter(((List) obj).get(i));
            }
        }
        for(Field field : obj.getClass().getDeclaredFields()){
            for(Annotation annotation : field.getAnnotations()){
                if(annotation instanceof FilterOut){
                    Object value = null;
                    try{
                        PropertyDescriptor propDesc= new PropertyDescriptor(field.getName(),obj.getClass());
                        propDesc.getWriteMethod().invoke(obj, (Object)null);
                        value = propDesc.getReadMethod().invoke(obj);
                    }catch(Exception ex){
                        LOGGER.warn("exception when filter out field: " + field.getName());
                    }
                }else if(annotation instanceof InnerFilter){
                    try{
                        Object sub = new PropertyDescriptor(field.getName(),
                                obj.getClass()).getReadMethod().invoke(obj);
                        if(sub != null){
                            postFilter(sub);
                        }
                    }catch (Exception ex){
                        LOGGER.warn("exception when filter out inner field: " + field.getName());
                    }
                }
            }
        }
    }
}
