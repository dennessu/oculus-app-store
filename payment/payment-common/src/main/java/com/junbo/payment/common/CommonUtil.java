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
import com.junbo.common.shuffle.Oculus48Id;
import com.junbo.payment.common.exception.AppClientExceptions;
import com.junbo.payment.common.exception.AppServerExceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.codec.binary.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;


/**
 * Common Util.
 */
public final class CommonUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtil.class);
    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

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

    public static Boolean toBool(String value){
        if(value == null){
            return null;
        }
        if(value.equalsIgnoreCase("Unknown")){
            return null;
        }
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

    public static String encode(Object value) {
        try{
            if (value instanceof Long) {
                Oculus48Id.validateRawValue((Long) value);
                return Oculus48Id.format(Oculus48Id.shuffle((Long) value));
            } else {
                return value == null ? "" : value.toString();
            }
        }catch (Exception ex){
            throw AppServerExceptions.INSTANCE.invalidIdToEncode(value == null ? "" : value.toString()).exception();
        }
    }

    public static Long decode(String id) {
        Oculus48Id.validateEncodedValue(id);
        return Oculus48Id.unShuffle(Oculus48Id.deFormat(id));
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
                        throw AppClientExceptions.INSTANCE.fieldNotNeeded(field.getName()).exception();
                    }
                    if(value != null){
                        throw AppClientExceptions.INSTANCE.fieldNotNeeded(field.getName()).exception();
                    }
                }else if(annotation instanceof InnerFilter){
                    try{
                        Object sub = new PropertyDescriptor(field.getName(),
                                obj.getClass()).getReadMethod().invoke(obj);
                        if(sub != null){
                            preValidation(sub);
                        }
                    }catch (Exception ex){
                        throw AppClientExceptions.INSTANCE.fieldNotNeeded(field.getName()).exception();
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

    public static String calHMCASHA1(String data, String key){
        String result = null;
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(data.getBytes());
            result = Base64.encodeBase64String(rawHmac);
            return result;
        }catch (Exception ex){
            LOGGER.error(ex.toString());
            throw AppServerExceptions.INSTANCE.errorCalculateHMCA().exception();
        }
    }
}
