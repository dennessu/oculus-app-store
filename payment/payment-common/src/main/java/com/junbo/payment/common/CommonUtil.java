/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.SerializeWriter;


import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;


/**
 * Common Util.
 */
public final class CommonUtil {

    private CommonUtil(){

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

    public static boolean isNullOrEmpty(String value){
        return value == null || value.isEmpty();
    }

    public static <T> List<T> filter(List<T> target, IPredicate<T> predicate) {
        List<T> result = new ArrayList<T>();
        for (T element: target) {
            if (predicate.apply(element)) {
                result.add(element);
            }
        }
        return result;
    }

    public static void preValidation(Object obj) {
        for(Field field : obj.getClass().getDeclaredFields()){
            for(Annotation annotation : field.getAnnotations()){
                if(annotation instanceof FilterIn){
                    Object value = null;
                    try{
                        value = new PropertyDescriptor(field.getName(), obj.getClass()).getReadMethod().invoke(obj);
                    }catch (Exception ex){
                        throw new PreValidationException(field.getName());
                    }
                    if(value != null){
                        throw new PreValidationException(field.getName());
                    }
                }else if(annotation instanceof InnerFilter){
                    try{
                        Object sub = new PropertyDescriptor(field.getName(),
                                obj.getClass()).getReadMethod().invoke(obj);
                        if(sub != null){
                            preValidation(sub);
                        }
                    }catch (Exception ex){
                        throw new PreValidationException(field.getName());
                    }
                }
            }
        }
    }

    public static void postFilter(Object obj) {
        for(Field field : obj.getClass().getDeclaredFields()){
            for(Annotation annotation : field.getAnnotations()){
                if(annotation instanceof FilterOut){
                    Object value = null;
                    try{
                        PropertyDescriptor propDesc= new PropertyDescriptor(field.getName(),obj.getClass());
                        propDesc.getWriteMethod().invoke(obj, (Object)null);
                        value = propDesc.getReadMethod().invoke(obj);
                    }catch(Exception ex){
                        throw new PostFilterOutException(field.getName());
                    }
                    if(value != null){
                        throw new PostFilterOutException(field.getName());
                    }
                }else if(annotation instanceof InnerFilter){
                    try{
                        Object sub = new PropertyDescriptor(field.getName(),
                                obj.getClass()).getReadMethod().invoke(obj);
                        if(sub != null){
                            postFilter(sub);
                        }
                    }catch (Exception ex){
                        throw new PostFilterOutException(field.getName());
                    }
                }
            }
        }
    }
}
