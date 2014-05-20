/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.common.util;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PlaceholderUtils.
 */
public class PlaceholderUtils {
    private static final String PATTERN_STRING ="\\{([^\\{\\}\\s]+)?\\}";
    private static final Pattern PATTERN = Pattern.compile(PATTERN_STRING);
    private PlaceholderUtils() {}

    public static String replace(String template, Map<String,String> data) {
        if(data == null || !hasPlaceholder(template)) {
            return template;
        }
        Map<String, String> map = new HashMap<>();
        for(String key : data.keySet()) {
            map.put(key.toLowerCase(),data.get(key));
        }
        StringBuffer sb = new StringBuffer();
        Matcher matcher = PATTERN.matcher(template);
        while (matcher.find()) {
            String name = matcher.group(1);
            String key = name != null ? name.toLowerCase() : null;
            String value = map.get(key);
            if(value != null) {
                matcher.appendReplacement(sb,value);
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static List<String> retrievePlaceholders(String template) {
        List<String> list = new ArrayList<>();
        if(hasPlaceholder(template)) {
            Matcher matcher = PATTERN.matcher(template);
            while (matcher.find()) {
                list.add(matcher.group(1));
            }
        }
        return list;
    }

    public static boolean hasPlaceholder(String template) {
        Matcher matcher = PATTERN.matcher(template);
        return matcher.find();
    }

    public static boolean compare(List<String> subList, List<String> supList) {
        if(subList == null || subList.size() == 0) {
            return true;
        }
        if(supList == null || supList.size() == 0) {
            return false;
        }
        List<String> list1 = new ArrayList<>();
        List<String> list2 = new ArrayList<>();
        for(String sub : subList) {
            if(!StringUtils.isEmpty(sub)) {
                list1.add(sub.toLowerCase());
            }
        }
        for(String sup : supList) {
            if(!StringUtils.isEmpty(sup)) {
                list2.add(sup.toLowerCase());
            }
        }
        return list2.containsAll(list1);
    }
}
