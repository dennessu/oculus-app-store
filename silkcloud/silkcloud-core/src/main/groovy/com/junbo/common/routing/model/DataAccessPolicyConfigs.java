/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.routing.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DataCenters.
 */
public class DataAccessPolicyConfigs {
    private static final Logger logger = LoggerFactory.getLogger(DataAccessPolicyConfigs.class);
    private static final Object none = new Object();

    // map from action:resource to name to data access policy
    private Map<String, DataAccessPolicy> dataAccessPolicies;
    private Map<String, DataAccessAction> httpMethodActions;

    private ConcurrentMap<String, Object> resolvedPolicies;

    public DataAccessPolicyConfigs(String dataAccessConfigs, String httpMethodMappingConfigs) {
        parseDataAccessConfigs(dataAccessConfigs);
        parseHttpMethodMapping(httpMethodMappingConfigs);
        resolvedPolicies = new ConcurrentHashMap<>();
    }

    public DataAccessPolicy getDataAccessPolicy(DataAccessAction action, Class<?> resource) {
        DataAccessPolicy result;

        String cacheKey = getCacheKey(action, resource);
        result = readCache(cacheKey);
        if (result != null) {
            return result;
        }

        result = dataAccessPolicies.get(getKey(action, resource.getName()));
        if (result == null) {
            result = dataAccessPolicies.get(getKey(action, resource.getPackage().getName()));
        }

        return addToCache(cacheKey, result);
    }

    public DataAccessPolicy getHttpDataAccessPolicy(String httpMethod, Class<?> resource) {
        DataAccessPolicy result;

        String cacheKey = getCacheKey(httpMethod, resource);
        result = readCache(cacheKey);
        if (result != null) {
            return result;
        }

        DataAccessAction action = getAction(httpMethod, resource.getName(), false);
        if (action == null) {
            action = getAction(httpMethod, resource.getPackage().getName(), true);
        }
        result = getDataAccessPolicy(action, resource);
        return addToCache(cacheKey, result);
    }

    private String getKey(DataAccessAction action, String resource) {
        return action.toString() + ":" + resource;
    }

    private String getHttpKey(String httpMethod, String resource) {
        return httpMethod + ":" + resource;
    }

    private String getCacheKey(DataAccessAction action, Class resource) {
        return ":" + action + ":" + resource.getName();
    }

    private String getCacheKey(String httpMethod, Class resource) {
        return httpMethod + ":" + resource.getName();
    }

    private DataAccessPolicy addToCache(String key, DataAccessPolicy value) {
        if (value == null) {
            resolvedPolicies.put(key, none);
        } else {
            resolvedPolicies.put(key, value);
        }
        return value;
    }

    private DataAccessPolicy readCache(String key) {
        Object result = resolvedPolicies.get(key);
        if (result == none) {
            return null;
        }
        return (DataAccessPolicy)result;
    }

    private DataAccessAction getAction(String httpMethod, String resource, boolean fallback) {
        httpMethod = httpMethod.toUpperCase();
        DataAccessAction action = httpMethodActions.get(getHttpKey(httpMethod, resource));
        if (action == null && fallback) {
            // fallback to defaults
            switch (httpMethod) {
                case "GET":
                case "HEAD":
                case "OPTIONS":
                    action = DataAccessAction.READ;
                    break;
                case "POST":
                case "PUT":
                case "DELETE":
                case "PATCH":
                    action = DataAccessAction.WRITE;
                    break;
                default:
                    break;
            }

            if (action == null) {
                throw new RuntimeException("Unknown http method action: " + httpMethod);
            }
        }
        return action;
    }

    private static final String DATAACCESS_REGEX_SINGLE_ENTRY = "\\s*(?<action>[^\\s:,]+)\\s*:\\s*(?<resource>[^\\s:,]+)\\s*:\\s*(?<policy>[^\\s:,]+)\\s*(,|$|(,\\s*$))";
    private static final String DATAACCESS_REGEX_FULL = "^(" + DATAACCESS_REGEX_SINGLE_ENTRY + ")*$";
    private static final Pattern DATAACCESS_PATTERN_SINGLE_ENTRY = Pattern.compile(DATAACCESS_REGEX_SINGLE_ENTRY);
    private static final Pattern DATAACCESS_PATTERN_FULL = Pattern.compile(DATAACCESS_REGEX_FULL);
    private void parseDataAccessConfigs(String dataAccessConfigs) {
        if (!DATAACCESS_PATTERN_FULL.matcher(dataAccessConfigs).matches()) {
            throw new RuntimeException("Invalid dataaccess configuration: " + dataAccessConfigs);
        }
        dataAccessPolicies = new HashMap<>();

        Matcher matcher = DATAACCESS_PATTERN_SINGLE_ENTRY.matcher(dataAccessConfigs);
        while (matcher.find()) {
            DataAccessAction action = Enum.valueOf(DataAccessAction.class, matcher.group("action"));
            String resource = matcher.group("resource");
            DataAccessPolicy policy = Enum.valueOf(DataAccessPolicy.class, matcher.group("policy"));

            logger.debug("Found dataaccess config: {} {} {}", action, resource, policy);

            String key = getKey(action, resource);
            if (dataAccessPolicies.containsKey(key)) {
                throw new RuntimeException("Duplicated action:resource name: " + key);
            }

            dataAccessPolicies.put(key, policy);
        }
    }

    private static final String HTTPMETHOD_REGEX_SINGLE_ENTRY = "\\s*(?<method>[^\\s:,]+)\\s*:\\s*(?<resource>[^\\s:,]+)\\s*:\\s*(?<action>[^\\s:,]+)\\s*(,|$|(,\\s*$))";
    private static final String HTTPMETHOD_REGEX_FULL = "^(" + HTTPMETHOD_REGEX_SINGLE_ENTRY + ")*$";
    private static final Pattern HTTPMETHOD_PATTERN_SINGLE_ENTRY = Pattern.compile(HTTPMETHOD_REGEX_SINGLE_ENTRY);
    private static final Pattern HTTPMETHOD_PATTERN_FULL = Pattern.compile(HTTPMETHOD_REGEX_FULL);
    private void parseHttpMethodMapping(String httpMethodMappingConfigs) {
        if (!HTTPMETHOD_PATTERN_FULL.matcher(httpMethodMappingConfigs).matches()) {
            throw new RuntimeException("Invalid dataaccess http method configuration: " + httpMethodMappingConfigs);
        }
        httpMethodActions = new HashMap<>();

        Matcher matcher = HTTPMETHOD_PATTERN_SINGLE_ENTRY.matcher(httpMethodMappingConfigs);
        while (matcher.find()) {
            String method = matcher.group("method");
            String resource = matcher.group("resource");
            DataAccessAction action = Enum.valueOf(DataAccessAction.class, matcher.group("action"));

            logger.debug("Found dataaccess action: {} {} {}", method, resource, action);

            String key = getHttpKey(method, resource);
            if (httpMethodActions.containsKey(key)) {
                throw new RuntimeException("Duplicated method:resource name: " + key);
            }

            httpMethodActions.put(key, action);
        }
    }
}
