/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.error;

import com.junbo.configuration.ConfigService;
import com.junbo.configuration.ConfigServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The map from component namespace to AppError component ID.
 */
public class Components {
    private static final Logger logger = LoggerFactory.getLogger(Components.class);

    private static Components instance = loadComponents();

    public static Components instance() {
        return instance;
    }

    private Map<String, String> packageToId;
    private String defaultComponent;

    public Components(String componentConfigs, String defaultComponent) {
        parseComponents(componentConfigs);
        this.defaultComponent = defaultComponent;
    }

    public String getComponent(Class<?> resource) {
        if (resource == null) {
            return defaultComponent;
        }
        String packageName = resource.getPackage().getName();
        String component = packageToId.get(packageName);
        if (component == null) {
            logger.warn("Component not found for package: " + packageName);
            return defaultComponent;
        }
        return component;
    }

    private void parseComponents(String componentConfigs) {
        final String componentSingleEntryRegex = "\\s*(?<id>[^\\s:,]+)\\s*:\\s*(?<package>[^\\s:,]+)\\s*(,|$|(,\\s*$))";
        final String componentRegex = "^(" + componentSingleEntryRegex + ")*$";
        final Pattern componentSingleEntryPattern = Pattern.compile(componentSingleEntryRegex);
        final Pattern componentPattern = Pattern.compile(componentRegex);

        if (!componentPattern.matcher(componentConfigs).matches()) {
            throw new RuntimeException("Invalid apperror component configuration: " + componentConfigs);
        }
        packageToId = new HashMap<>();

        Matcher matcher = componentSingleEntryPattern.matcher(componentConfigs);
        while (matcher.find()) {
            String id = matcher.group("id");
            String packageName = matcher.group("package");

            logger.debug("Found apperror component config: {} {}", id, packageName);

            if (packageToId.containsKey(packageName)) {
                throw new RuntimeException("AppErrors duplicated package name: " + packageName);
            }

            packageToId.put(packageName, id);
        }
    }

    private static Components loadComponents() {
        ConfigService configService = ConfigServiceManager.instance();
        String componentConfigs = configService.getConfigValue("apperrors.components");
        String defaultComponent = configService.getConfigValue("apperrors.defaultComponent");

        return new Components(componentConfigs, defaultComponent);
    }
}
